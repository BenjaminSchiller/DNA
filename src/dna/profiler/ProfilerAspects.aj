package dna.profiler;

import java.io.IOException;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.IGraphGenerator;
import dna.metrics.Metric;
import dna.metrics.Metric.ApplicationType;
import dna.profiler.Profiler.ProfilerType;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.MetricData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.Update;
import dna.util.Config;

public aspect ProfilerAspects {
	private static Stack<String> formerCountKey = new Stack<>(); 
	private String currentCountKey;

	private long currentBatchTimestamp;
	
	private String batchGeneratorName;
	public static final String initialAddition = Config.get("PROFILER_INITIALBATCH_KEYADDITION");

	pointcut seriesSingleRunGeneration(Series s, int run) : execution(* SeriesGeneration.generateRun(Series, int, ..)) && args(s, run, ..);
	pointcut startInitialBatchGeneration(Series s) : execution(* SeriesGeneration.generateInitialData(Series)) && args(s);
	pointcut startNewBatchGeneration(Series s, long timestamp) : execution(* SeriesGeneration.generateNextBatch(Series, long)) && args(s, timestamp);
	pointcut seriesGeneration() : execution(* SeriesGeneration.generate(..));
	
	pointcut graphGeneration(IGraphGenerator graphGenerator) : execution(* IGraphGenerator+.generate()) && target(graphGenerator);
	pointcut graphGenerated(): cflow(graphGeneration(*));
	
	pointcut batchGeneration(BatchGenerator batchGenerator) : execution(* BatchGenerator+.generate(*)) && target(batchGenerator);
	pointcut batchGenerated() : cflow(batchGeneration(*));
	
	pointcut initialMetric(Metric metricObject) : execution(* Metric+.compute()) && target(metricObject);
	pointcut metricAppliedOnUpdate(Metric metricObject, Update updateObject) : (execution(* Metric+.applyBeforeUpdate(Update+))
			 || execution(* Metric+.applyAfterUpdate(Update+))) && args(updateObject) && target(metricObject);
	pointcut metricAppliedOnBatch(Metric metricObject, Update batchObject) : (execution(* Metric+.applyBeforeBatch(Batch+))
			 || execution(* Metric+.applyAfterBatch(Batch+))) && args(batchObject) && target(metricObject);
	pointcut metricApplied() : cflow(initialMetric(*)) || cflow(metricAppliedOnUpdate(*, *)) || cflow(metricAppliedOnBatch(*, *));
	pointcut writeMetric(MetricData md, String dir) : call(* MetricData.write(String)) && args(dir) && target(md);
	
	pointcut updateApplication(Update updateObject) : execution(* Update+.apply(*)) && target(updateObject);
	pointcut updateApplied(): cflow(updateApplication(*));
	
	pointcut watchedCall() : graphGenerated() || batchGenerated() || metricApplied() || updateApplied();

	pointcut initGDS(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);

	pointcut add(DataStructure list) : call(* IDataStructure+.add(..)) && target(list) && watchedCall();
	pointcut remove(DataStructure list) : call(* IDataStructure+.remove(..)) && target(list) && watchedCall();
	pointcut contains(DataStructure list) : call(* IDataStructure+.contains(..)) && target(list) && watchedCall();
	pointcut getElement(DataStructure list) : call(* IDataStructure+.get(..)) && target(list) && watchedCall();
	pointcut size(DataStructure list) : call(* IDataStructure+.size()) && target(list) && watchedCall();
	pointcut random(DataStructure list) : call(* IDataStructure+.getRandom()) && target(list) && watchedCall();
	pointcut iterator(DataStructure list) : execution(* DataStructure+.iterator()) && target(list) && watchedCall();
	
	before(Series s, int run) : seriesSingleRunGeneration(s, run) {
		Profiler.setSeriesDir(s.getDir());
		Profiler.startRun(run);
		Profiler.startBatch(0);
	}
	
	after(Series s, int run) : seriesSingleRunGeneration(s, run) {
		Profiler.finishRun();
	}
	
	after(Series s) : startInitialBatchGeneration(s) {
		Profiler.finishBatch(0);
	}
	
	before(Series s, long timestamp) : startNewBatchGeneration(s, timestamp) {
		this.currentBatchTimestamp = timestamp;
		Profiler.startBatch(currentBatchTimestamp);
	}
	
	after(Series s, long timestamp) : startNewBatchGeneration(s, timestamp) {
		Profiler.finishBatch(currentBatchTimestamp);
	}
	
	
	after() : seriesGeneration() {
		Profiler.finishSeries();
	}

	Graph around(IGraphGenerator graphGenerator) : graphGeneration(graphGenerator) {
		formerCountKey.push(currentCountKey);
		currentCountKey = ((GraphGenerator) graphGenerator).getName();
		Profiler.setInInitialBatch(false);
		Profiler.setGraphGeneratorName(currentCountKey);
		Graph res = proceed(graphGenerator);
		currentCountKey = formerCountKey.pop();
		return res;
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
			currentCountKey += initialAddition;
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
	
	boolean around(Update updateObject) : updateApplication(updateObject) {
		formerCountKey.push(currentCountKey);
		currentCountKey = updateObject.getType().toString();
		Profiler.setInInitialBatch(false);
		boolean res = proceed(updateObject);
		currentCountKey = formerCountKey.pop();
		return res;
	}

	after(Graph g, GraphDataStructure gds) : initGDS(g, gds) {
		Profiler.init(gds);
	}
	
	after(DataStructure list): add(list) {
		if (list.listType == ListType.GlobalNodeList)
			Profiler.count(this.currentCountKey, ProfilerType.AddNodeGlobal);
		else if (list.listType == ListType.GlobalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.AddEdgeGlobal);
		else if (list.listType == ListType.LocalNodeList)
			Profiler.count(this.currentCountKey, ProfilerType.AddNodeLocal);
		else if (list.listType == ListType.LocalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.AddEdgeLocal);
	}	
	
	after(DataStructure list) : remove(list) {
		if (list.listType == ListType.GlobalNodeList)
			Profiler.count(currentCountKey, ProfilerType.RemoveNodeGlobal);
		else if (list.listType == ListType.GlobalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.RemoveEdgeGlobal);
		else if (list.listType == ListType.LocalNodeList)
			Profiler.count(currentCountKey, ProfilerType.RemoveNodeLocal);
		else if (list.listType == ListType.LocalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.RemoveEdgeLocal);
	}
	
	after(DataStructure list) : contains(list) {
		if (list.listType == ListType.GlobalNodeList)
			Profiler.count(currentCountKey, ProfilerType.ContainsNodeGlobal);
		else if (list.listType == ListType.GlobalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.ContainsEdgeGlobal);
		else if (list.listType == ListType.LocalNodeList)
			Profiler.count(currentCountKey, ProfilerType.ContainsNodeLocal);
		else if (list.listType == ListType.LocalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.ContainsEdgeLocal);
	}
	
	after(DataStructure list) : getElement(list) {
		if (list.listType == ListType.GlobalNodeList)
			Profiler.count(currentCountKey, ProfilerType.GetNodeGlobal);
		else if (list.listType == ListType.GlobalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.GetEdgeGlobal);
		else if (list.listType == ListType.LocalNodeList)
			Profiler.count(currentCountKey, ProfilerType.GetNodeLocal);
		else if (list.listType == ListType.LocalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.GetEdgeLocal);
	}	
	
	after(DataStructure list) : size(list) {
		if (list.listType == ListType.GlobalNodeList)
			Profiler.count(currentCountKey, ProfilerType.SizeNodeGlobal);
		else if (list.listType == ListType.GlobalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.SizeEdgeGlobal);
		else if (list.listType == ListType.LocalNodeList)
			Profiler.count(currentCountKey, ProfilerType.SizeNodeLocal);
		else if (list.listType == ListType.LocalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.SizeEdgeLocal);
	}
	
	after(DataStructure list) : random(list) {
		if (list.listType == ListType.GlobalNodeList)
			Profiler.count(currentCountKey, ProfilerType.RandomNodeGlobal);
		else if (list.listType == ListType.GlobalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.RandomEdgeGlobal);
	}
	
	after(DataStructure list) : iterator(list) {
		if (list.listType == ListType.GlobalNodeList)
			Profiler.count(currentCountKey, ProfilerType.IteratorNodeGlobal);
		else if (list.listType == ListType.GlobalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.IteratorEdgeGlobal);
		else if (list.listType == ListType.LocalNodeList)
			Profiler.count(currentCountKey, ProfilerType.IteratorNodeLocal);
		else if (list.listType == ListType.LocalEdgeList)
			Profiler.count(currentCountKey, ProfilerType.IteratorEdgeLocal);
	}

	after(MetricData md, String dir) throws IOException : writeMetric(md, dir) {
		Profiler.writeMetric(md.getName(), dir);		
	}

}
