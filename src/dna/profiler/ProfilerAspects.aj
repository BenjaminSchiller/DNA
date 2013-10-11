package dna.profiler;

import java.io.IOException;
import java.util.Stack;

import dna.graph.Element;
import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.io.filesystem.Files;
import dna.metrics.Metric;
import dna.metrics.Metric.ApplicationType;
import dna.profiler.Profiler.ProfilerType;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.Config;

public aspect ProfilerAspects {
	private static boolean isActive = false;
	private static Stack<String> formerMetric = new Stack<>(); 
	private String currentMetric;
	public static final String initialAddition = Config.get("PROFILER_INITIALBATCH_KEYADDITION");

	pointcut activate() : execution(* Profiler.activate());

	pointcut newBatch() : execution(BatchData.new(..));
	pointcut aggregateDataPerRun(Series s, int run) : execution(* SeriesGeneration.generateRun(Series, int, ..)) && args(s, run, ..) && if(isActive);
	pointcut aggregateDataOverAllRuns(Series s) : execution(* SeriesGeneration.generate(Series, int, int, boolean, boolean)) && args(s, ..) && if(isActive);
	
	pointcut initialMetric(Metric metricObject) : execution(* Metric+.compute()) && target(metricObject);
	pointcut metricAppliedOnUpdate(Metric metricObject, Update updateObject) : (execution(* Metric+.applyBeforeUpdate(Update+))
			 || execution(* Metric+.applyAfterUpdate(Update+))) && args(updateObject) && target(metricObject);
	pointcut metricAppliedOnBatch(Metric metricObject, Update batchObject) : (execution(* Metric+.applyBeforeBatch(Batch+))
			 || execution(* Metric+.applyAfterBatch(Batch+))) && args(batchObject) && target(metricObject);
	pointcut metricApplied() : cflow(initialMetric(*)) || cflow(metricAppliedOnUpdate(*, *)) || cflow(metricAppliedOnBatch(*, *));
	pointcut writeMetric(MetricData md, String dir) : call(* MetricData.write(String)) && args(dir) && target(md) && if(isActive);
	
	pointcut updateApplication(Update updateObject) : execution(* Update+.apply(*)) && target(updateObject);
	pointcut updateApplied(): cflow(updateApplication(*));
	
	pointcut watchedCall() : metricApplied() || updateApplied();
	
	pointcut seriesFinished() : execution(* SeriesGeneration.generate(..)) && if(isActive);

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
	
	pointcut writeData(String dir) : call(* BatchData.write(String)) && args(dir) && if(isActive);

	before() : newBatch() {
		Profiler.reset();
	}
	
	after() : activate() {
		isActive = true;
	}

	boolean around(Metric metricObject) : initialMetric(metricObject) {
		formerMetric.push(currentMetric);
		currentMetric = metricObject.getName();
		Profiler.setInInitialBatch(false);
		if (metricObject.getApplicationType() != ApplicationType.Recomputation) {
			currentMetric += initialAddition;
			Profiler.setInInitialBatch(true);
		}
		boolean res = proceed(metricObject);
		currentMetric = formerMetric.pop();
		return res;
	}

	boolean around(Metric metricObject, Update updateObject) : metricAppliedOnUpdate(metricObject, updateObject) {
		formerMetric.push(currentMetric);
		currentMetric = metricObject.getName();
		Profiler.setInInitialBatch(false);
		boolean res = proceed(metricObject, updateObject);
		currentMetric = formerMetric.pop();
		return res;
	}
	
	boolean around(Update updateObject) : updateApplication(updateObject) {
		formerMetric.push(currentMetric);
		currentMetric = updateObject.getType().toString();
		Profiler.setInInitialBatch(false);
		boolean res = proceed(updateObject);
		currentMetric = formerMetric.pop();
		return res;
	}

	after(Graph g, GraphDataStructure gds) : init(g, gds) {
		Profiler.init(gds);
	}
	
	after() : seriesFinished() {
//		Profiler.finish();
	}

	after() : nodeAdd() && graphAction() {
		Profiler.count(this.currentMetric, ProfilerType.AddNodeGlobal);
	}

	after() : nodeAdd() && nodeAction() {
		Profiler.count(this.currentMetric, ProfilerType.AddNodeLocal);
	}

	after() : edgeAdd() && graphAction()  {
		Profiler.count(currentMetric, ProfilerType.AddEdgeGlobal);
	}

	after() : edgeAdd() && nodeAction()  {
		Profiler.count(currentMetric, ProfilerType.AddEdgeLocal);
	}

	after() : nodeRemove() && graphAction()  {
		Profiler.count(currentMetric, ProfilerType.RemoveNodeGlobal);
	}

	after() : nodeRemove() && nodeAction()  {
		Profiler.count(currentMetric, ProfilerType.RemoveNodeLocal);
	}

	after() : edgeRemove() && graphAction()  {
		Profiler.count(currentMetric, ProfilerType.RemoveEdgeGlobal);
	}

	after() : edgeRemove() && nodeAction()  {
		Profiler.count(currentMetric, ProfilerType.RemoveEdgeLocal);
	}

	after() : nodeContains() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.ContainsNodeGlobal);
	}

	after() : nodeContains() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.ContainsNodeLocal);
	}

	after() : edgeContains() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.ContainsEdgeGlobal);
	}

	after() : edgeContains() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.ContainsEdgeLocal);
	}

	after() : nodeGet() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.GetNodeGlobal);
	}

	after() : nodeGet() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.GetNodeLocal);
	}

	after() : edgeGet() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.GetEdgeGlobal);
	}

	after() : edgeGet() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.GetEdgeLocal);
	}	
	
	after() : nodeSize() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.SizeNodeGlobal);
	}

	after() : nodeSize() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.SizeNodeLocal);
	}

	after() : edgeSize() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.SizeEdgeGlobal);
	}

	after() : edgeSize() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.SizeEdgeLocal);
	}

	after() : nodeRandom() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.RandomNodeGlobal);
	}

	after() : edgeRandom() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.RandomEdgeGlobal);
	}
	
	after() : nodeIterator() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.IteratorNodeGlobal);
	}

	after() : nodeIterator() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.IteratorNodeLocal);
	}
	
	after() : edgeIterator() && graphAction() {
		Profiler.count(currentMetric, ProfilerType.IteratorEdgeGlobal);
	}

	after() : edgeIterator() && nodeAction() {
		Profiler.count(currentMetric, ProfilerType.IteratorEdgeLocal);
	}	
	
	after(MetricData md, String dir) throws IOException : writeMetric(md, dir) {
		Profiler.writeSingle(md.getName(), dir, Files.getProfilerFilename(Config.get("METRIC_PROFILER")));
	}
	
	after(String dir) throws IOException : writeData(dir) {
		Profiler.write(dir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")));
	}

	after(Series s) throws IOException : aggregateDataOverAllRuns(s) {
		String seriesDir = s.getDir();
		Profiler.aggregate(seriesDir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")));
	}
}
