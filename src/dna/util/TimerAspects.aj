package dna.util;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.datastructures.config.DSConfig;
import dna.graph.datastructures.hotswap.Hotswap;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.IGraphGenerator;
import dna.metrics.Metric;
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
import dna.series.Aggregation;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.SeriesStats;
import dna.series.aggdata.AggregatedSeries;
import dna.series.data.BatchData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.lists.RunTimeList;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.IBatchGenerator;
import dna.updates.update.Update;

public aspect TimerAspects {

	/**
	 * GRAPH GENERATION (measures 'graphGeneration')
	 */

	private Timer graphGeneration;

	pointcut graphGeneration(GraphGenerator gg) : target(gg) && (
			call(* IGraphGenerator+.generate(..))
			);

	Graph around(GraphGenerator gg) : graphGeneration(gg) {
		// System.out.println("GENERATING graph  " + gg.getName());

		Timer t = new Timer(SeriesStats.graphGenerationRuntime);
		Graph res = proceed(gg);
		t.end();
		this.graphGeneration = t;
		return res;
	}

	/**
	 * BATCH GENERATION (measures 'batchGeneration')
	 */

	private Timer batchGeneration;

	pointcut batchGeneration(BatchGenerator bg) : target(bg) && (
			call(* IBatchGenerator+.generate(..))
			);

	Batch around(BatchGenerator bg) : batchGeneration(bg) {
		// System.out.println("GENERATING BATCH " + bg.getName());

		Timer t = new Timer(SeriesStats.batchGenerationRuntime);
		Batch res = proceed(bg);
		t.end();
		this.batchGeneration = t;
		return res;
	}

	/**
	 * GRAPH UPDATE (measures 'graphUpdate')
	 */

	private Timer graphUpdate;

	pointcut graphUpdate(Update u) : target(u) && call(* Update+.apply(..));

	boolean around(Update u) : graphUpdate(u) {
		// System.out.println("APPLICATION OF UPDATE " + u);

		Timer t = this.graphUpdate;
		if (t == null) {
			t = new Timer(SeriesStats.graphUpdateRuntime);
		}
		t.restart();
		boolean res = proceed(u);
		t.end();
		this.graphUpdate = t;
		return res;
	}

	/**
	 * METRICS (measures 'metrics' and 'separate metric runtimes')
	 */

	pointcut initialMetricData(): call(* SeriesGeneration.computeInitialMetrics(..));

	private HashMap<String, Timer> metricTimers;

	// metric initialization (init or first recompute)

	pointcut metricInit(Metric m) : target(m) && cflow(initialMetricData()) && (
			call(* IDynamicAlgorithm+.init()) ||
			call(* IRecomputation+.recompute())
			);

	public static String firstInitMetric = null;

	Object around(Metric m): metricInit(m) {
		Timer t = null;
		if (firstInitMetric == null) {
			firstInitMetric = m.getName();
			t = new Timer(m.getName());
		}
		Object res = proceed(m);
		if (t != null) {
			t.end();
			this.metricTimers.put(m.getName(), t);
			firstInitMetric = null;
			// System.out
			// .println("INIT: " + m.getName() + " -> " + t.getRuntime());
		}
		return res;

		// Timer t = new Timer(m.getName());
		// Object res = proceed(m);
		// t.end();
		// this.metricTimers.put(m.getName(), t);
		// return res;
	}

	// metric recomputation

	pointcut metricRecomputation(Metric m) : target(m) && !cflow(initialMetricData()) && (
			call(* IRecomputation+.recompute())
			);

	public static String firstRecomputationMetric = null;

	boolean around(Metric m) : metricRecomputation(m){
		Timer t = null;
		if (firstRecomputationMetric == null) {
			// System.out.println();
			firstRecomputationMetric = m.getName();
			t = this.metricTimers.get(m.getName());
			if (t == null) {
				t = new Timer(m.getName());
				// System.out.println("new: " + t.getRuntime());
				// } else {
				// System.out.println("old: " + t.getRuntime());
			}
			// System.out.println("START: " + m.getName() + " with "
			// + t.getRuntime());
			t.restart();
		}
		boolean res = proceed(m);
		if (t != null) {
			t.end();
			this.metricTimers.put(m.getName(), t);
			firstRecomputationMetric = null;
//			System.out.println("RECOMP: " + m.getName() + " -> "
//					+ t.getRuntime());
		}
		return res;
		// Timer t = new Timer(m.getName());
		// boolean res = proceed(m);
		// t.end();
		// this.metricTimers.put(m.getName(), t);
		// return res;
	}

	// metric update application

	pointcut metricUpdate(Metric m, Update u) : args(u) && target(m) && (
			call(* IBeforeEA+.applyBeforeUpdate(Update+)) ||
			call(* IBeforeER+.applyBeforeUpdate(Update+)) ||
			call(* IBeforeEW+.applyBeforeUpdate(Update+)) ||
			call(* IBeforeNA+.applyBeforeUpdate(Update+)) ||
			call(* IBeforeNR+.applyBeforeUpdate(Update+)) ||
			call(* IBeforeNW+.applyBeforeUpdate(Update+)) ||
			call(* IAfterEA+.applyAfterUpdate(Update+)) ||
			call(* IAfterER+.applyAfterUpdate(Update+)) ||
			call(* IAfterEW+.applyAfterUpdate(Update+)) ||
			call(* IAfterNA+.applyAfterUpdate(Update+)) ||
			call(* IAfterNR+.applyAfterUpdate(Update+)) ||
			call(* IAfterNW+.applyAfterUpdate(Update+))
			);

	public static String firstUpdateMetric = null;

	boolean around(Metric m, Update u) : metricUpdate(m, u){
		Timer t = null;
		if (firstUpdateMetric == null) {
			firstUpdateMetric = m.getName();
			t = this.metricTimers.get(m.getName());
			if (t == null) {
				t = new Timer(m.getName());
				// System.out.println("new-update: " + t.getRuntime());
			}
			// System.out.println(firstUpdateMetric
			// + " - UPDATE (before / after) " + u + " for " + m.getName()
			// + " @ " + t.getDutation());
			t.restart();
		}
		boolean res = proceed(m, u);
		if (t != null) {
			t.end();
			this.metricTimers.put(m.getName(), t);
			firstUpdateMetric = null;
		}
		return res;

		// Timer t = this.metricTimers.get(m.getName());
		// if (t == null) {
		// t = new Timer(m.getName());
		// }
		// t.restart();
		// boolean res = proceed(m, u);
		// t.end();
		// this.metricTimers.put(m.getName(), t);
		// return res;
	}

	// metric batch application

	pointcut metricBatch(Metric m, Batch b) : args(b) && target(m) && (
			call(* IBeforeBatch+.applyBeforeBatch(Batch+)) ||
			call(* IAfterBatch+.applyAfterBatch(Batch+))
			);

	boolean around(Metric m, Batch b) : metricBatch(m, b){
		// System.out.println("BATCH (before / after) for " + m.getName());

		Timer t = this.metricTimers.get(m.getName());
		if (t == null) {
			t = new Timer(m.getName());
			// System.out.println("new-batch: " + t.getRuntime());
			// } else {
			// System.out.println("old-batch: " + t.getRuntime());
		}

		t.restart();
		boolean res = proceed(m, b);
		t.end();

		this.metricTimers.put(m.getName(), t);

		return res;
	}

	/**
	 * SERIES (outputs total for series)
	 */

	pointcut seriesGeneration() : (
			call(* SeriesGeneration.generate(Series, int, int, boolean, boolean, boolean, long))
			);

	SeriesData around() : seriesGeneration() {
		// System.out.println("STARTING SERIES GENERATION...");

		Timer t = new Timer("seriesGeneration");
		SeriesData res = proceed();
		t.end();
		Log.info("total time for seriesGeneration: " + t.toString());
		Log.infoSep();
		return res;
	}

	/**
	 * RUN (initializes list, outputs total for run)
	 */

	pointcut runGeneration() : (
			call(* SeriesGeneration.generateRun(Series, int, int, boolean, boolean, long))
			);

	void around() : runGeneration() {
		// System.out.println("STARTING RUN GENERATION...");

		this.metricTimers = new HashMap<String, Timer>();
		Timer t = new Timer("runGeneration");
		proceed();
		t.end();
		Log.info(t.toString());
	}

	/**
	 * BATCH (measures 'total')
	 */

	private Timer total;

	pointcut batch() : (
			call(* SeriesGeneration.computeInitialData(..)) ||
			call(* SeriesGeneration.computeNextBatch(..))
			);

	BatchData around(): batch() {
		// System.out.println("STARTING BATCH....");

		Timer t = new Timer(SeriesStats.totalRuntime);
		BatchData res = proceed();
		t.end();
		this.total = t;
		return res;
	}

	/**
	 * AGGREGATION (outputs total for aggregation)
	 */

	pointcut aggregation(SeriesData sd) : args(sd) && (
			call(* Aggregation.aggregateSeries(SeriesData))
			);

	AggregatedSeries around(SeriesData sd) : aggregation(sd) {
		Timer t = new Timer("aggregation");
		AggregatedSeries res = proceed(sd);
		t.end();
		Log.info("Aggregation: " + t.toString());
		return res;
	}

	/**
	 * ADD RUNTIMES (adds runtimes to current run)
	 */

	pointcut addRuntimes() : (
			call(* SeriesGeneration.generateInitialData(..)) ||
			call(* SeriesGeneration.generateNextBatch(..))
			);

	BatchData around() : addRuntimes() {
		BatchData res = proceed();
		RunTimeList rt = res.getGeneralRuntimes();

		// System.out.println("ADD RUNTIMES...");

		// graphGeneration
		if (this.graphGeneration != null) {
			rt.add(this.graphGeneration.getRuntime());
		} else {
			rt.add(new RunTime(SeriesStats.graphGenerationRuntime, 0));
		}

		// batchGeneration
		if (this.batchGeneration != null) {
			rt.add(this.batchGeneration.getRuntime());
		} else {
			rt.add(new RunTime(SeriesStats.batchGenerationRuntime, 0));
		}

		// graphUpdate
		if (this.graphUpdate != null) {
			rt.add(this.graphUpdate.getRuntime());
		} else {
			rt.add(new RunTime(SeriesStats.graphUpdateRuntime, 0));
		}

		// metrics
		long metrics = 0;
		for (Timer t : this.metricTimers.values()) {
			// each metric...
			res.getMetricRuntimes().add(t.getRuntime());
			metrics += t.getDutation();
		}
		rt.add(new RunTime(SeriesStats.metricsRuntime, metrics));

		long total = 0;
		if (this.total != null)
			total += this.total.getDutation();
		if (this.recommendation != null)
			total += this.recommendation.getDutation();
		if (this.hotswap != null)
			total += this.hotswap.getDutation();

		// total
		if (this.total != null) {
			rt.add(new RunTime(SeriesStats.totalRuntime, total));
		} else {
			rt.add(new RunTime(SeriesStats.totalRuntime, 0));
		}

		// hotSwap
		if (this.hotswap != null) {
			rt.add(new RunTime(SeriesStats.hotswapRuntime, this.hotswap
					.getDutation()));
		} else {
			rt.add(new RunTime(SeriesStats.hotswapRuntime, 0));
		}
		if (this.recommendation != null) {
			rt.add(new RunTime(SeriesStats.recommendationRuntime,
					this.recommendation.getDutation()));
		} else {
			rt.add(new RunTime(SeriesStats.recommendationRuntime, 0));
		}

		// profiler
		if (this.profiler != null) {
			rt.add(this.profiler.getRuntime());
		} else {
			rt.add(new RunTime(SeriesStats.profilerRuntime, 0));
		}

		// sum
		long sum = 0;
		if (this.graphGeneration != null)
			sum += this.graphGeneration.getDutation();
		if (this.batchGeneration != null)
			sum += this.batchGeneration.getDutation();
		if (this.graphUpdate != null)
			sum += this.graphUpdate.getDutation();
		sum += metrics;
		if (this.hotswap != null)
			sum += this.hotswap.getDutation();
		if (this.recommendation != null)
			sum += this.recommendation.getDutation();
		if (this.profiler != null)
			sum += this.profiler.getDutation();
		rt.add(new RunTime(SeriesStats.sumRuntime, sum));

		// overhead
		long overhead = total - sum;
		rt.add(new RunTime(SeriesStats.overheadRuntime, overhead));

		// reset timers
		this.graphGeneration = null;
		this.batchGeneration = null;
		this.graphUpdate = null;
		this.total = null;
		this.hotswap = null;
		this.recommendation = null;
		this.profiler = null;
		this.metricTimers.clear();

		return res;
	}

	/**
	 * PROFILER
	 */

	private Timer profiler;

	pointcut profiler(): (
			execution(* Profiler.start*(..)) || 
			execution(* Profiler.finish*(..))
			);

	Object around(): profiler() {
		// System.out.println("PROFILER...");

		Timer t = this.profiler;
		if (t == null) {
			t = new Timer(SeriesStats.profilerRuntime);
		}

		t.restart();
		Object res = proceed();
		t.end();
		this.profiler = t;

		return res;
	}

	/**
	 * HOTSWAP
	 */

	private Timer hotswap;

	pointcut hotswap() :
		call(* Hotswap.execute(..));

	boolean around(): hotswap() {
		if (this.hotswap == null) {
			this.hotswap = new Timer();
		}
		this.hotswap.restart();
		boolean res = proceed();
		this.hotswap.end();
		return res;
	}

	/**
	 * RECOMMENDATION
	 */

	private Timer recommendation;

	pointcut recommendation() :
		call(* Hotswap.recommendConfig(..));

	DSConfig around(): recommendation() {
		if (this.recommendation == null) {
			this.recommendation = new Timer();
		}
		this.recommendation.restart();
		DSConfig res = proceed();
		this.recommendation.end();
		return res;
	}

}
