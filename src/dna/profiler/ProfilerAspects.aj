package dna.profiler;

import java.io.IOException;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.IGraphGenerator;
import dna.metrics.Metric;
import dna.metrics.Metric.ApplicationType;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.Update;

public aspect ProfilerAspects {
	private static Stack<String> formerCountKey = new Stack<>(); 
	private String currentCountKey;
	private Graph currentGraph;

	private String batchGeneratorName;
	
	private boolean inAdd, addFailedAsContainsReturnsTrue;

	pointcut seriesSingleRunGeneration(Series s, int run, int numberOfBatches) : execution(* SeriesGeneration.generateRun(Series, int, int, ..)) && args(s, run, numberOfBatches, ..);
	pointcut batchGenerationCallee(Series s) : (execution(* SeriesGeneration.generateInitialData(Series)) || execution(* SeriesGeneration.generateNextBatch(Series))) && args(s);
	pointcut seriesGeneration() : execution(* SeriesGeneration.generate(..));
	
	pointcut graphGeneration(IGraphGenerator graphGenerator) : execution(* IGraphGenerator+.generate()) && target(graphGenerator);
	pointcut graphGenerated(): cflow(graphGeneration(*));
	
	pointcut batchGeneration(BatchGenerator batchGenerator) : execution(* BatchGenerator+.generate(*)) && target(batchGenerator);
	pointcut batchGenerated() : cflow(batchGeneration(*));
	
	pointcut initialMetric(Metric metricObject) : execution(* Metric+.compute()) && target(metricObject);
	pointcut metricAppliedOnUpdate(Metric metricObject, Update updateObject) : (execution(* Metric+.applyBeforeUpdate(Update+))
			 || execution(* Metric+.applyAfterUpdate(Update+))) && args(updateObject) && target(metricObject);
	pointcut metricAppliedOnBatch(Metric metricObject, Batch batchObject) : (execution(* Metric+.applyBeforeBatch(Batch+))
			 || execution(* Metric+.applyAfterBatch(Batch+))) && args(batchObject) && target(metricObject);
	pointcut metricApplied() : cflow(initialMetric(*)) || cflow(metricAppliedOnUpdate(*, *)) || cflow(metricAppliedOnBatch(*, *));
	
	pointcut updateApplication(Update updateObject) : execution(* Update+.apply(*)) && target(updateObject);
	pointcut updateApplied(): cflow(updateApplication(*));
	
	pointcut watchedCall() : graphGenerated() || batchGenerated() || metricApplied() || updateApplied();

	pointcut initGDS(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);

	pointcut init(DataStructure list, boolean firstTime) : call(* IDataStructure+.init(Class, int, boolean)) && target(list) && args(*,*, firstTime) && watchedCall();
	pointcut add(DataStructure list) : call(* IDataStructure+.add(..)) && target(list) && watchedCall();
	pointcut remove(DataStructure list) : call(* IDataStructure+.remove(..)) && target(list) && watchedCall();
	pointcut contains(DataStructure list) : call(* IDataStructure+.contains(..)) && target(list) && watchedCall();
	pointcut getElement(DataStructure list) : call(* IDataStructure+.get(..)) && target(list) && watchedCall();
	pointcut size(DataStructure list) : call(* IDataStructure+.size()) && target(list) && watchedCall();
	pointcut random(DataStructure list) : call(* IDataStructure+.getRandom()) && target(list) && watchedCall();
	pointcut iterator(DataStructure list) : execution(* DataStructure+.iterator()) && target(list) && watchedCall();
	
	before(Series s, int run, int numberOfBatches) : seriesSingleRunGeneration(s, run, numberOfBatches) {
		Profiler.setSeriesData(s, numberOfBatches);
		Profiler.startRun(run);
		HotSwap.reset();
	}
	
	after(Series s, int run, int numberOfBatches) : seriesSingleRunGeneration(s, run, numberOfBatches) {
		try {
			Profiler.finishRun();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	BatchData around(Series s):	batchGenerationCallee(s) {
		Profiler.startBatch();
		BatchData res = proceed(s);
		long currentBatchTimestamp = s.getGraph().getTimestamp();
		try {
			Profiler.finishBatch(currentBatchTimestamp, res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	
	after() : seriesGeneration() {
		try {
			Profiler.finishSeries();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Graph around(IGraphGenerator graphGenerator) : graphGeneration(graphGenerator) {
		formerCountKey.push(currentCountKey);
		currentCountKey = ((GraphGenerator) graphGenerator).getName();
		Profiler.setInInitialBatch(false);
		Profiler.setGraphGeneratorName(currentCountKey);
		currentGraph = proceed(graphGenerator);
		currentCountKey = formerCountKey.pop();
		return currentGraph;
	}
	
	Batch around(BatchGenerator batchGenerator) : batchGeneration(batchGenerator) {
		formerCountKey.push(currentCountKey);
		batchGeneratorName = batchGenerator.getName();
		Profiler.addBatchGeneratorName(batchGeneratorName);
		currentCountKey = batchGeneratorName;
		Profiler.setInInitialBatch(false);
		Batch res = proceed(batchGenerator);
		currentCountKey = formerCountKey.pop();
		return res;
	}

	boolean around(Metric metricObject) : initialMetric(metricObject) {
		formerCountKey.push(currentCountKey);
		currentCountKey = metricObject.getName();
		Profiler.addMetricName(currentCountKey);
		Profiler.setInInitialBatch(false);
		if (metricObject.getApplicationType() != ApplicationType.Recomputation) {
			currentCountKey += Profiler.initialBatchKeySuffix;
			Profiler.setInInitialBatch(true);
		}
		boolean res = proceed(metricObject);
		currentCountKey = formerCountKey.pop();
		return res;
	}

	boolean around(Metric metricObject, Update updateObject) : metricAppliedOnUpdate(metricObject, updateObject) {
		formerCountKey.push(currentCountKey);
		currentCountKey = metricObject.getName();
		Profiler.addMetricName(currentCountKey);
		Profiler.setInInitialBatch(false);
		boolean res = proceed(metricObject, updateObject);
		currentCountKey = formerCountKey.pop();
		return res;
	}
	
	boolean around(Metric metricObject, Batch batchObject) : metricAppliedOnBatch(metricObject, batchObject) {
		formerCountKey.push(currentCountKey);
		currentCountKey = metricObject.getName();
		Profiler.addMetricName(currentCountKey);
		Profiler.setInInitialBatch(false);
		boolean res = proceed(metricObject, batchObject);
		currentCountKey = formerCountKey.pop();
		return res;
	}	
	
	boolean around(Update updateObject) : updateApplication(updateObject) {
		formerCountKey.push(currentCountKey);
		currentCountKey = updateObject.getType().toString();
		Profiler.setInInitialBatch(false);
		boolean res = proceed(updateObject);
		currentCountKey = formerCountKey.pop();
		return res;
	}

	after(Graph g, GraphDataStructure gds) : initGDS(g, gds) {
		Profiler.init(g, gds);
	}

	after(DataStructure list, boolean firstTime): init(list, firstTime) {
		if (firstTime)
			Profiler.count(this.currentCountKey, list.listType, AccessType.Init);
	}
	
	boolean around(DataStructure list): add(list) {
		inAdd = true;
		addFailedAsContainsReturnsTrue = false;
		boolean res = proceed(list);
		inAdd = false;
		if (!addFailedAsContainsReturnsTrue)
			Profiler.count(this.currentCountKey, list.listType, AccessType.Add);
		return res;
	}	
	
	boolean around(DataStructure list) : remove(list) {
		boolean res = proceed(list);
		if (res) {
			Profiler.count(this.currentCountKey, list.listType,
					AccessType.RemoveSuccess);
		} else {
			Profiler.count(this.currentCountKey, list.listType,
					AccessType.RemoveFailure);
		}
		return res;
	}
	
	boolean around(DataStructure list) : contains(list) {
		boolean res = proceed(list);
		if (res) {
			Profiler.count(this.currentCountKey, list.listType, AccessType.ContainsSuccess);
			if (inAdd)
				addFailedAsContainsReturnsTrue = true;
		}
		else
			Profiler.count(this.currentCountKey, list.listType, AccessType.ContainsFailure);
		return res;
	}
	
	Object around(DataStructure list) : getElement(list) {
		Object res = proceed(list);
		if (res == null)
			Profiler.count(this.currentCountKey, list.listType,
					AccessType.GetFailure);
		else
			Profiler.count(this.currentCountKey, list.listType,
					AccessType.GetSuccess);
		return res;
	}	
	
	after(DataStructure list) : size(list) {
		Profiler.count(this.currentCountKey, list.listType, AccessType.Size);
	}
	
	after(DataStructure list) : random(list) {
		Profiler.count(this.currentCountKey, list.listType, AccessType.Random);
	}
	
	after(DataStructure list) : iterator(list) {
		Profiler.count(this.currentCountKey, list.listType, AccessType.Iterator);
	}

}
