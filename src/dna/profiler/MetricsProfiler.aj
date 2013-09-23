package dna.profiler;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.metrics.Metric;
import dna.profiler.GraphProfiler.ProfilerType;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.series.data.RunData;
import dna.updates.Batch;
import dna.updates.Update;
import dna.util.Config;

public aspect MetricsProfiler {
	private static boolean isActive = true;
	private String currentMetric;
	public static final String initialAddition = ".initialBatch";

	pointcut activate() : execution(* GraphProfiler.activate());

	pointcut newBatch() : execution(BatchData.new(..));
	pointcut aggregateDataPerRun(Series s, int run) : execution(* SeriesGeneration.generateRun(Series, int, ..)) && args(s, run, ..);
	pointcut aggregateDataOverAllRuns(Series s) : execution(* SeriesGeneration.generate(Series, int, int, boolean, boolean)) && args(s, ..);
	
	
	pointcut initialMetric(Metric metricObject) : execution(* Metric+.compute()) && target(metricObject);
	pointcut metricAppliedOnUpdate(Metric metricObject, Update<?> updateObject) : (execution(* Metric+.applyBeforeUpdate(Update+))
			 || execution(* Metric+.applyAfterUpdate(Update+))) && args(updateObject) && target(metricObject);
	pointcut metricAppliedOnBatch(Metric metricObject, Update<?> batchObject) : (execution(* Metric+.applyBeforeBatch(Batch+))
			 || execution(* Metric+.applyAfterBatch(Batch+))) && args(batchObject) && target(metricObject);
	pointcut metricApplied() : cflow(initialMetric(*)) || cflow(metricAppliedOnUpdate(*, *)) || cflow(metricAppliedOnBatch(*, *));
	
	pointcut seriesFinished() : execution(* SeriesGeneration.generate(..)) && if(isActive);

	pointcut init(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);

	pointcut nodeAdd() : call(* INodeListDatastructure+.add(Node+)) && cflow(metricApplied()) && if(isActive);
	pointcut nodeRemove() : call(* INodeListDatastructure+.remove(Node+)) && cflow(metricApplied()) && if(isActive);
	pointcut nodeContains() : call(* INodeListDatastructure+.contains(Node+)) && cflow(metricApplied()) && if(isActive);
	pointcut nodeSize() : call(* INodeListDatastructure+.size()) && cflow(metricApplied()) && if(isActive);
	pointcut nodeRandom() : call(* INodeListDatastructure+.getRandom()) && cflow(metricApplied()) && if(isActive);

	pointcut edgeAdd() : call(* IEdgeListDatastructure+.add(Edge+)) && cflow(metricApplied()) && if(isActive);
	pointcut edgeRemove() : call(* IEdgeListDatastructure+.remove(Edge+)) && cflow(metricApplied()) && if(isActive);
	pointcut edgeContains() : call(* IEdgeListDatastructure+.contains(Edge+)) && cflow(metricApplied()) && if(isActive);
	pointcut edgeSize() : call(* IEdgeListDatastructure+.size()) && cflow(metricApplied()) && if(isActive);
	pointcut edgeRandom() : call(* IEdgeListDatastructure+.getRandom()) && cflow(metricApplied()) && if(isActive);
	
	pointcut graphAction() : this(Graph);
	pointcut nodeAction() : this(Node);
	
	pointcut writeData(String dir) : call(* BatchData.write(String)) && args(dir) && if(isActive);

	before() : newBatch() {
		GraphProfiler.reset();
	}
	
	after() : activate() {
		isActive = true;
	}

	boolean around(Metric metricObject) : initialMetric(metricObject) {
		currentMetric = metricObject.getName() + initialAddition;
		boolean res = proceed(metricObject);
		currentMetric = null;
		return res;
	}

	boolean around(Metric metricObject, Update<?> updateObject) : metricAppliedOnUpdate(metricObject, updateObject) {
		currentMetric = metricObject.getName();
		boolean res = proceed(metricObject, updateObject);
		currentMetric = null;
		return res;
	}

	after(Graph g, GraphDataStructure gds) : init(g, gds) {
		GraphProfiler.init(gds);
	}
	
	after() : seriesFinished() {
//		GraphProfiler.finish();
	}

	after() : nodeAdd() && graphAction() {
		GraphProfiler.count(this.currentMetric, ProfilerType.AddNodeGlobal);
	}

	after() : nodeAdd() && nodeAction() {
		GraphProfiler.count(this.currentMetric, ProfilerType.AddNodeLocal);
	}

	after() : edgeAdd() && graphAction()  {
		GraphProfiler.count(currentMetric, ProfilerType.AddEdgeGlobal);
	}

	after() : edgeAdd() && nodeAction()  {
		GraphProfiler.count(currentMetric, ProfilerType.AddEdgeLocal);
	}

	after() : nodeRemove() && graphAction()  {
		GraphProfiler.count(currentMetric, ProfilerType.RemoveNodeGlobal);
	}

	after() : nodeRemove() && nodeAction()  {
		GraphProfiler.count(currentMetric, ProfilerType.RemoveNodeLocal);
	}

	after() : edgeRemove() && graphAction()  {
		GraphProfiler.count(currentMetric, ProfilerType.RemoveEdgeGlobal);
	}

	after() : edgeRemove() && nodeAction()  {
		GraphProfiler.count(currentMetric, ProfilerType.RemoveEdgeLocal);
	}

	after() : nodeContains() && graphAction() {
		GraphProfiler.count(currentMetric, ProfilerType.ContainsNodeGlobal);
	}

	after() : nodeContains() && nodeAction() {
		GraphProfiler.count(currentMetric, ProfilerType.ContainsNodeLocal);
	}

	after() : edgeContains() && graphAction() {
		GraphProfiler.count(currentMetric, ProfilerType.ContainsEdgeGlobal);
	}

	after() : edgeContains() && nodeAction() {
		GraphProfiler.count(currentMetric, ProfilerType.ContainsEdgeLocal);
	}

	after() : nodeSize() && graphAction() {
		GraphProfiler.count(currentMetric, ProfilerType.SizeNodeGlobal);
	}

	after() : nodeSize() && nodeAction() {
		GraphProfiler.count(currentMetric, ProfilerType.SizeNodeLocal);
	}

	after() : edgeSize() && graphAction() {
		GraphProfiler.count(currentMetric, ProfilerType.SizeEdgeGlobal);
	}

	after() : edgeSize() && nodeAction() {
		GraphProfiler.count(currentMetric, ProfilerType.SizeEdgeLocal);
	}

	after() : nodeRandom() && graphAction() {
		GraphProfiler.count(currentMetric, ProfilerType.RandomNodeGlobal);
	}

	after() : edgeRandom() && graphAction() {
		GraphProfiler.count(currentMetric, ProfilerType.RandomEdgeGlobal);
	}

	after(String dir) throws IOException : writeData(dir) {
		GraphProfiler.write(dir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")));
	}

	after(Series s) throws IOException : aggregateDataOverAllRuns(s) {
		String seriesDir = s.getDir();
		System.out.println("Writing to " + seriesDir);
		GraphProfiler.aggregate(seriesDir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")));
	}
}
