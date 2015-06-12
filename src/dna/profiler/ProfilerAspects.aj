package dna.profiler;

import java.io.IOException;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.generators.IGraphGenerator;
import dna.metrics.Metric;
import dna.metrics.algorithms.Algorithms;
import dna.metrics.algorithms.IAfterBatch;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IAfterEW;
import dna.metrics.algorithms.IAfterNA;
import dna.metrics.algorithms.IAfterNR;
import dna.metrics.algorithms.IAfterNW;
import dna.metrics.algorithms.IBeforeBatch;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeER;
import dna.metrics.algorithms.IBeforeEW;
import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeNR;
import dna.metrics.algorithms.IBeforeNW;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.metrics.algorithms.IRecomputation;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.Update;

public aspect ProfilerAspects {
	// private static Stack<String> formerCountKey = new Stack<>();
	// private String currentCountKey;
	// private Graph currentGraph;
	//
	// private boolean inAdd, addFailedAsContainsReturnsTrue;
	//
	// /*
	// * SERIES generation
	// */
	//
	// pointcut seriesGeneration() : execution(* SeriesGeneration.generate(..));
	//
	// after() : seriesGeneration() {
	// System.out.println("XX seriesGeneration - finishing (after)");
	// try {
	// Profiler.finishSeries();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /*
	// * RUN generation
	// */
	//
	// pointcut runGeneration(Series s, int run, int batches) :
	// args(s, run, batches, ..) &&
	// execution(* SeriesGeneration.generateRun(Series, int, int, ..));
	//
	// before(Series s, int run, int batches) : runGeneration(s, run, batches) {
	// // System.out.println("XXXXXX seriesSingleRunGeneration - before");
	// Profiler.setSeriesData(s, batches);
	// Profiler.startRun(run);
	// HotSwap.reset();
	// }
	//
	// after(Series s, int run, int batches) : runGeneration(s, run, batches) {
	// // System.out.println("XXXXXX seriesSingleRunGeneration - after");
	// try {
	// Profiler.finishRun();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /*
	// * BATCH generation
	// */
	//
	// pointcut seriesBatchGeneration(Series s, Algorithms a) :
	// args(s, a) && (
	// execution(* SeriesGeneration.generateInitialData(Series, Algorithms)) ||
	// execution(* SeriesGeneration.generateNextBatch(Series, Algorithms))
	// );
	//
	// BatchData around(Series s, Algorithms a): seriesBatchGeneration(s, a) {
	// // System.out.println("XXXXXXX seriesBatchGeneration - before");
	// Profiler.startBatch();
	// BatchData res = proceed(s, a);
	// long currentBatchTimestamp = s.getGraph().getTimestamp();
	// try {
	// Profiler.finishBatch(currentBatchTimestamp, res);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// // System.out.println("XXXXXXX seriesBatchGeneration - after");
	// return res;
	// }
	//
	// pointcut batchGeneration(BatchGenerator bg) :
	// target(bg) &&
	// execution(* BatchGenerator+.generate(*));
	//
	// Batch around(BatchGenerator bg) : batchGeneration(bg) {
	// // System.out.println("XXX batchGeneration - before (" + bg.getName()
	// // + ")");
	// formerCountKey.push(currentCountKey);
	// String name = bg.getName();
	// Profiler.addBatchGeneratorName(name);
	// currentCountKey = name;
	// Profiler.setInInitialBatch(false);
	// Batch res = proceed(bg);
	// currentCountKey = formerCountKey.pop();
	// // System.out
	// // .println("XXX batchGeneration - after (" + bg.getName() + ")");
	// return res;
	// }
	//
	// pointcut batchGenerated() :
	// cflow(batchGeneration(*));
	//
	// /*
	// * GRAPH generation
	// */
	//
	// pointcut graphGeneration(IGraphGenerator gg) :
	// target(gg) &&
	// execution(* IGraphGenerator+.generate());
	//
	// Graph around(IGraphGenerator gg) : graphGeneration(gg) {
	// // System.out.println("XX graphGeneration - before");
	// formerCountKey.push(currentCountKey);
	// currentCountKey = gg.getName();
	// Profiler.setInInitialBatch(false);
	// Profiler.setGraphGeneratorName(currentCountKey);
	// currentGraph = proceed(gg);
	// currentCountKey = formerCountKey.pop();
	// // System.out.println("XX graphGeneration - after");
	// return currentGraph;
	// }
	//
	// pointcut graphGenerated() :
	// cflow(graphGeneration(*));
	//
	// /*
	// * METRICS init / re-computation
	// */
	//
	// pointcut metricRecomputation(Metric m) :
	// target(m) &&
	// execution(* IRecomputation+.recompute());
	//
	// boolean around(Metric m) : metricRecomputation(m) {
	// // System.out.println("YYY metric recomputation - " + m.getName()
	// // + " - before");
	// formerCountKey.push(currentCountKey);
	// currentCountKey = m.getName();
	// Profiler.addMetricName(currentCountKey);
	// Profiler.setInInitialBatch(false);
	// boolean res = proceed(m);
	// currentCountKey = formerCountKey.pop();
	// // System.out.println("YYY metric recomputation - " + m.getName()
	// // + " - after");
	// return res;
	// }
	//
	// pointcut metricInit(Metric m) :
	// target(m) &&
	// execution(* IDynamicAlgorithm+.init());
	//
	// boolean around(Metric m) : metricInit(m) {
	// // System.out.println("YYY metric init - " + m.getName() + " - before");
	// formerCountKey.push(currentCountKey);
	// currentCountKey = m.getName();
	// Profiler.addMetricName(currentCountKey);
	// Profiler.setInInitialBatch(false);
	// currentCountKey += Profiler.initialBatchKeySuffix;
	// Profiler.setInInitialBatch(true);
	// boolean res = proceed(m);
	// currentCountKey = formerCountKey.pop();
	// // System.out.println("YYY metric init - " + m.getName() + " - after");
	// return res;
	// }
	//
	// /*
	// * METRIC using updates
	// */
	//
	// pointcut metricUpdate(Metric m, Update u) :
	// target(m) &&
	// args(u) &&
	// (
	// execution(* IBeforeNA+.applyBeforeUpdate(Update+)) ||
	// execution(* IBeforeNR+.applyBeforeUpdate(Update+)) ||
	// execution(* IBeforeNW+.applyBeforeUpdate(Update+)) ||
	// execution(* IBeforeEA+.applyBeforeUpdate(Update+)) ||
	// execution(* IBeforeER+.applyBeforeUpdate(Update+)) ||
	// execution(* IBeforeEW+.applyBeforeUpdate(Update+)) ||
	// execution(* IAfterNA+.applyAfterUpdate(Update+)) ||
	// execution(* IAfterNR+.applyAfterUpdate(Update+)) ||
	// execution(* IAfterNW+.applyAfterUpdate(Update+)) ||
	// execution(* IAfterEA+.applyAfterUpdate(Update+)) ||
	// execution(* IAfterER+.applyAfterUpdate(Update+)) ||
	// execution(* IAfterEW+.applyAfterUpdate(Update+))
	// );
	//
	// boolean around(Metric m, Update u) : metricUpdate(m, u) {
	// // System.out.println("ZZZZZZZZZZ " + m.getName() + "...." +
	// // u.asString()
	// // + " - before");
	// formerCountKey.push(currentCountKey);
	// currentCountKey = m.getName();
	// Profiler.addMetricName(currentCountKey);
	// Profiler.setInInitialBatch(false);
	// boolean res = proceed(m, u);
	// currentCountKey = formerCountKey.pop();
	// // System.out.println("ZZZZZZZZZZ " + m.getName() + "...." +
	// // u.asString()
	// // + " - after");
	// return res;
	// }
	//
	// /*
	// * METRIC using batches
	// */
	//
	// pointcut metricBatch(Metric m, Batch b) :
	// target(m) &&
	// args(b) &&
	// (
	// execution(* IBeforeBatch+.applyBeforeBatch(Batch)) ||
	// execution(* IAfterBatch+.applyAfterBatch(Batch))
	// );
	//
	// boolean around(Metric m, Batch b) : metricBatch(m, b) {
	// // System.out.println("ZZZZZZZZZ " + m.getName() +
	// // ".....BATCH - before");
	// formerCountKey.push(currentCountKey);
	// currentCountKey = m.getName();
	// Profiler.addMetricName(currentCountKey);
	// Profiler.setInInitialBatch(false);
	// boolean res = proceed(m, b);
	// currentCountKey = formerCountKey.pop();
	// System.out.println("ZZZZZZZZZ " + m.getName() + ".....BATCH - after");
	// return res;
	// }
	//
	// pointcut metricApplied() :
	// cflow(metricInit(*)) ||
	// cflow(metricRecomputation(*)) ||
	// cflow(metricUpdate(*, *)) ||
	// cflow(metricBatch(*, *));
	//
	// /*
	// * UPDATE application
	// */
	//
	// pointcut updateApplication(Update updateObject) :
	// target(updateObject) &&
	// execution(* Update+.apply(*));
	//
	// pointcut updateApplied():
	// cflow(updateApplication(*));
	//
	// pointcut watchedCall() :
	// graphGenerated() ||
	// batchGenerated() ||
	// metricApplied() ||
	// updateApplied();
	//
	// boolean around(Update updateObject) : updateApplication(updateObject) {
	// formerCountKey.push(currentCountKey);
	// currentCountKey = updateObject.getType().toString();
	// Profiler.setInInitialBatch(false);
	// boolean res = proceed(updateObject);
	// currentCountKey = formerCountKey.pop();
	// return res;
	// }
	//
	// /*
	// * PROFILER init
	// */
	//
	// pointcut initProfiler(Graph g, GraphDataStructure gds) :
	// this(g) &&
	// args(*,*,gds,..) &&
	// execution(Graph+.new(String, long, GraphDataStructure, ..));
	//
	// after(Graph g, GraphDataStructure gds) : initProfiler(g, gds) {
	// Profiler.init(g, gds);
	// }
	//
	// /*
	// * DS counters
	// */
	//
	// // init
	//
	// pointcut init(DataStructure list, boolean firstTime) :
	// call(* IDataStructure+.init(Class, int, boolean)) &&
	// target(list) &&
	// args(*,*,firstTime) &&
	// watchedCall();
	//
	// after(DataStructure list, boolean firstTime): init(list, firstTime) {
	// if (firstTime) {
	// Profiler.count(this.currentCountKey, list.listType, AccessType.Init);
	// }
	// }
	//
	// // add
	//
	// pointcut add(DataStructure list) :
	// call(* IDataStructure+.add(..)) &&
	// target(list) &&
	// watchedCall();
	//
	// boolean around(DataStructure list): add(list) {
	// inAdd = true;
	// addFailedAsContainsReturnsTrue = false;
	// boolean res = proceed(list);
	// inAdd = false;
	// if (!addFailedAsContainsReturnsTrue) {
	// Profiler.count(this.currentCountKey, list.listType, AccessType.Add);
	// }
	// return res;
	// }
	//
	// // remove
	//
	// pointcut remove(DataStructure list) :
	// call(* IDataStructure+.remove(..)) &&
	// target(list) &&
	// watchedCall();
	//
	// boolean around(DataStructure list) : remove(list) {
	// boolean res = proceed(list);
	// if (res) {
	// Profiler.count(this.currentCountKey, list.listType,
	// AccessType.RemoveSuccess);
	// } else {
	// Profiler.count(this.currentCountKey, list.listType,
	// AccessType.RemoveFailure);
	// }
	// return res;
	// }
	//
	// // contains
	//
	// pointcut contains(DataStructure list) :
	// call(* IDataStructure+.contains(..)) &&
	// target(list) &&
	// watchedCall();
	//
	// boolean around(DataStructure list) : contains(list) {
	// boolean res = proceed(list);
	// if (res) {
	// Profiler.count(this.currentCountKey, list.listType,
	// AccessType.ContainsSuccess);
	// if (inAdd)
	// addFailedAsContainsReturnsTrue = true;
	// } else
	// Profiler.count(this.currentCountKey, list.listType,
	// AccessType.ContainsFailure);
	// return res;
	// }
	//
	// // get
	//
	// pointcut getElement(DataStructure list) :
	// call(* IDataStructure+.get(..)) &&
	// target(list) &&
	// watchedCall();
	//
	// Object around(DataStructure list) : getElement(list) {
	// Object res = proceed(list);
	// if (res == null)
	// Profiler.count(this.currentCountKey, list.listType,
	// AccessType.GetFailure);
	// else
	// Profiler.count(this.currentCountKey, list.listType,
	// AccessType.GetSuccess);
	// return res;
	// }
	//
	// // size
	//
	// pointcut size(DataStructure list) :
	// call(* IDataStructure+.size()) &&
	// target(list) &&
	// watchedCall();
	//
	// after(DataStructure list) : size(list) {
	// Profiler.count(this.currentCountKey, list.listType, AccessType.Size);
	// }
	//
	// // getRandom
	//
	// pointcut random(DataStructure list) :
	// call(* IDataStructure+.getRandom()) &&
	// target(list) &&
	// watchedCall();
	//
	// after(DataStructure list) : random(list) {
	// Profiler.count(this.currentCountKey, list.listType, AccessType.Random);
	// }
	//
	// // iterator
	//
	// pointcut iterator(DataStructure list) :
	// execution(* DataStructure+.iterator()) &&
	// target(list) &&
	// watchedCall();
	//
	// after(DataStructure list) : iterator(list) {
	// Profiler.count(this.currentCountKey, list.listType, AccessType.Iterator);
	// }

}
