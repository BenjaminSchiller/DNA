package dna.profiler;

import java.io.IOException;
import java.util.Stack;

import dna.graph.Element;
import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.IGraphGenerator;
import dna.graph.nodes.Node;
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
	private static boolean isActive = false;
	private static Stack<String> formerCountKey = new Stack<>(); 
	private String currentCountKey;

	private long currentBatchTimestamp;
	
	private String batchGeneratorName;
	public static final String initialAddition = Config.get("PROFILER_INITIALBATCH_KEYADDITION");

	pointcut activate() : execution(* Profiler.activate());

	pointcut seriesSingleRunGeneration(Series s, int run) : execution(* SeriesGeneration.generateRun(Series, int, ..)) && args(s, run, ..) && if(isActive);
	pointcut startInitialBatchGeneration(Series s) : execution(* SeriesGeneration.generateInitialData(Series)) && args(s) && if(isActive);
	pointcut startNewBatchGeneration(Series s, long timestamp) : execution(* SeriesGeneration.generateNextBatch(Series, long)) && args(s, timestamp) && if(isActive);
	pointcut seriesGeneration() : execution(* SeriesGeneration.generate(..)) && if(isActive);
	
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
	pointcut writeMetric(MetricData md, String dir) : call(* MetricData.write(String)) && args(dir) && target(md) && if(isActive);
	
	pointcut updateApplication(Update updateObject) : execution(* Update+.apply(*)) && target(updateObject);
	pointcut updateApplied(): cflow(updateApplication(*));
	
	pointcut watchedCall() : graphGenerated() || batchGenerated() || metricApplied() || updateApplied();
	

	pointcut init(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);

	pointcut nodeAdd() : call(* INodeListDatastructure+.add(Node+)) && watchedCall() && if(isActive);
	pointcut nodeRemove() : call(* INodeListDatastructure+.remove(Node+)) && watchedCall() && if(isActive);
	pointcut nodeContains() : call(* INodeListDatastructure+.contains(Node+)) && watchedCall() && if(isActive);
	pointcut nodeGet() : call(* INodeListDatastructure+.get(int)) && watchedCall() && if(isActive);	
	pointcut nodeSize() : call(* INodeListDatastructure+.size()) && watchedCall() && if(isActive);
	pointcut nodeRandom() : call(* INodeListDatastructure+.getRandom()) && watchedCall() && if(isActive);
	// Ignore the warning for the following line - everything works fine and as expected
	pointcut nodeIterator() : call(* INodeListDatastructure+.iterator()) && watchedCall() && if(isActive);

	pointcut edgeAdd() : call(* IEdgeListDatastructure+.add(Edge+)) && watchedCall() && if(isActive);
	pointcut edgeRemove() : call(* IEdgeListDatastructure+.remove(Edge+)) && watchedCall() && if(isActive);
	pointcut edgeContains() : call(* IEdgeListDatastructure+.contains(Edge+)) && watchedCall() && if(isActive);
	pointcut edgeGet() : call(* IEdgeListDatastructure+.get(Edge)) && watchedCall() && if(isActive);
	pointcut edgeSize() : call(* IEdgeListDatastructure+.size()) && watchedCall() && if(isActive);
	pointcut edgeRandom() : call(* IEdgeListDatastructure+.getRandom()) && watchedCall() && if(isActive);

	// Ignore the warning for the following line - everything works fine and as expected	
	pointcut edgeIterator() : call(* IEdgeListDatastructure+.iterator()) && watchedCall() && if(isActive);
	
	pointcut graphAction() : !within(Element+);
	pointcut nodeAction() : within(Element+);
	
	after() : activate() {
		isActive = true;
	}
	
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

	after(Graph g, GraphDataStructure gds) : init(g, gds) {
		Profiler.init(gds);
	}

	after() : nodeAdd() && graphAction() {
		Profiler.count(this.currentCountKey, ProfilerType.AddNodeGlobal);
	}

	after() : nodeAdd() && nodeAction() {
		Profiler.count(this.currentCountKey, ProfilerType.AddNodeLocal);
	}

	after() : edgeAdd() && graphAction()  {
		Profiler.count(currentCountKey, ProfilerType.AddEdgeGlobal);
	}

	after() : edgeAdd() && nodeAction()  {
		Profiler.count(currentCountKey, ProfilerType.AddEdgeLocal);
	}

	after() : nodeRemove() && graphAction()  {
		Profiler.count(currentCountKey, ProfilerType.RemoveNodeGlobal);
	}

	after() : nodeRemove() && nodeAction()  {
		Profiler.count(currentCountKey, ProfilerType.RemoveNodeLocal);
	}

	after() : edgeRemove() && graphAction()  {
		Profiler.count(currentCountKey, ProfilerType.RemoveEdgeGlobal);
	}

	after() : edgeRemove() && nodeAction()  {
		Profiler.count(currentCountKey, ProfilerType.RemoveEdgeLocal);
	}

	after() : nodeContains() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.ContainsNodeGlobal);
	}

	after() : nodeContains() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.ContainsNodeLocal);
	}

	after() : edgeContains() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.ContainsEdgeGlobal);
	}

	after() : edgeContains() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.ContainsEdgeLocal);
	}

	after() : nodeGet() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.GetNodeGlobal);
	}

	after() : nodeGet() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.GetNodeLocal);
	}

	after() : edgeGet() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.GetEdgeGlobal);
	}

	after() : edgeGet() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.GetEdgeLocal);
	}	
	
	after() : nodeSize() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.SizeNodeGlobal);
	}

	after() : nodeSize() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.SizeNodeLocal);
	}

	after() : edgeSize() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.SizeEdgeGlobal);
	}

	after() : edgeSize() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.SizeEdgeLocal);
	}

	after() : nodeRandom() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.RandomNodeGlobal);
	}

	after() : edgeRandom() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.RandomEdgeGlobal);
	}
	
	after() : nodeIterator() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.IteratorNodeGlobal);
	}

	after() : nodeIterator() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.IteratorNodeLocal);
	}
	
	after() : edgeIterator() && graphAction() {
		Profiler.count(currentCountKey, ProfilerType.IteratorEdgeGlobal);
	}

	after() : edgeIterator() && nodeAction() {
		Profiler.count(currentCountKey, ProfilerType.IteratorEdgeLocal);
	}	
	
	after(MetricData md, String dir) throws IOException : writeMetric(md, dir) {
		Profiler.writeMetric(md.getName(), dir);		
	}

}
