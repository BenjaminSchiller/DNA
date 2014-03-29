package dna.util;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.generators.IGraphGenerator;
import dna.metrics.Metric;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.SeriesStats;
import dna.series.data.BatchData;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.lists.RunTimeList;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.Update;

public aspect TimerAspects {
	private HashSet<String> resetList = new HashSet<>();
	private HashSet<String> metricList = new HashSet<>();

	pointcut seriesGeneration() : call(* SeriesGeneration.generate(Series, int, int, boolean, boolean));
	pointcut runGeneration(): call(* SeriesGeneration.generateRun(Series, int, int,..));
	pointcut graphGeneration(): call(* IGraphGenerator.generate(..));

	pointcut initialBatchData(): call(* SeriesGeneration.computeInitialData(..)) || call(* SeriesGeneration.computeNextBatch(..));
	pointcut initialMetricData(): call(* SeriesGeneration.computeInitialMetrics(..));

	pointcut batchGeneration(): call(* BatchGenerator+.generate(..));
	pointcut batchApplication(): call(* Update+.apply(..));

	pointcut metricApplicationInInitialization(Metric metric) : (call(* Metric+.init()) || call(* Metric+.compute())) && target(metric) && cflow(initialMetricData());
	pointcut metricApplicationPerBatch(Metric metric, Batch b) : (call(* Metric+.applyBeforeBatch(Batch+))
			 || call(* Metric+.applyAfterBatch(Batch+))) && args(b) && target(metric);
	pointcut metricApplicationPerUpdate(Metric metric, Update update) : (call(* Metric+.applyBeforeUpdate(Update+))
			 || call(* Metric+.applyAfterUpdate(Update+))) && args(update) && target(metric);

	SeriesData around(): seriesGeneration() {
		Timer timer = new Timer("seriesGeneration");
		SeriesData res = proceed();
		timer.end();
		Log.info("total time for seriesGeneration: " + timer.toString());
		Log.infoSep();

		return res;
	}

	RunData around(): runGeneration() {
		Timer timer = new Timer("runGeneration");
		RunData res = proceed();
		timer.end();
		Log.info(timer.toString());

		return res;
	}

	Graph around(): graphGeneration() {
		resetList.add(SeriesStats.graphGenerationRuntime);
		Timer graphGenerationTimer = new Timer(
				SeriesStats.graphGenerationRuntime);
		Graph res = proceed();
		graphGenerationTimer.end();
		TimerMap.put(graphGenerationTimer);
		return res;
	}

	BatchData around(): initialBatchData() {
		for (String resetTimerName : resetList) {
			TimerMap.remove(resetTimerName);
		}

		Timer t = new Timer(SeriesStats.totalRuntime);
		BatchData res = proceed();
		t.end();
		TimerMap.put(t);
		
		res.getGeneralRuntimes().add(
				TimerMap.get(SeriesStats.totalRuntime).getRuntime());
		res.getGeneralRuntimes().add(
				TimerMap.get(SeriesStats.graphGenerationRuntime).getRuntime());
		res.getGeneralRuntimes().add(
				TimerMap.get(SeriesStats.batchGenerationRuntime).getRuntime());
		res.getGeneralRuntimes().add(
				TimerMap.get(SeriesStats.graphUpdateRuntime).getRuntime());		
		res.getGeneralRuntimes().add(
				TimerMap.get(SeriesStats.metricsRuntime).getRuntime());
		

		// add metric runtimes
		for (String m : metricList) {
			res.getMetricRuntimes().add(
					TimerMap.get(m).getRuntime());
		}
		
		RunTimeList generalRuntimes = res.getGeneralRuntimes();
		double total = generalRuntimes.get(SeriesStats.totalRuntime)
				.getRuntime();
		double metrics = generalRuntimes.get(SeriesStats.metricsRuntime)
				.getRuntime();
		
		long sumRt = 0;
		for (RunTime rt : res.getGeneralRuntimes().getList()) {
			sumRt += rt.getRuntime();
		}
		for (RunTime rt : res.getMetricRuntimes().getList()) {
			sumRt += rt.getRuntime();
		}
		
		double sum = sumRt - total - metrics;
		double overhead = total - sum;
		generalRuntimes.add(new RunTime("sum", sum));
		generalRuntimes.add(new RunTime("overhead", overhead));
		
		return res;
	}

	BatchData around(): initialMetricData() {
		Timer t = new Timer(SeriesStats.metricsRuntime);
		BatchData res = proceed();
		t.end();
		TimerMap.put(t);
		
		for (String metricName: metricList) {
			res.getMetricRuntimes().add(
					TimerMap.get(metricName).getRuntime());
		}
		
		return res;
	}

	Batch around(): batchGeneration() {
		Timer t = new Timer(SeriesStats.batchGenerationRuntime);
		Batch res = proceed();
		t.end();
		TimerMap.put(t);
		return res;
	}

	boolean around(): batchApplication() {
		resetList.add(SeriesStats.graphUpdateRuntime);
		Timer t = TimerMap.get(SeriesStats.graphUpdateRuntime);
		if (t == null) {
			t = new Timer(SeriesStats.graphUpdateRuntime);
		}
		t.restart();
		boolean res = proceed();
		t.end();
		TimerMap.put(t);
		return res;
	}

	Object around(Metric metric): metricApplicationInInitialization(metric) {
		String metricName = metric.getName();
		metricList.add(metricName);
		Timer t = new Timer(metricName);
		Object res = proceed(metric);
		t.end();
		TimerMap.put(t);
		return res;
	}

	boolean around(Metric metric, Batch b): metricApplicationPerBatch(metric, b) {
		resetList.add(metric.getName());
		Timer singleMetricTimer = TimerMap.get(metric.getName());
		if (singleMetricTimer == null) {
			singleMetricTimer = new Timer(metric.getName());
		}

		resetList.add(SeriesStats.metricsRuntime);
		Timer wholeMetricsTimer = TimerMap.get(SeriesStats.metricsRuntime);
		if (wholeMetricsTimer == null) {
			wholeMetricsTimer = new Timer(SeriesStats.metricsRuntime);
		}

		singleMetricTimer.restart();
		wholeMetricsTimer.restart();
		boolean res = proceed(metric, b);
		singleMetricTimer.end();
		wholeMetricsTimer.end();
		TimerMap.put(singleMetricTimer);
		TimerMap.put(wholeMetricsTimer);
		return res;
	}

	boolean around(Metric metric, Update u): metricApplicationPerUpdate(metric, u) {
		resetList.add(metric.getName());
		Timer singleMetricTimer = TimerMap.get(metric.getName());
		if (singleMetricTimer == null) {
			singleMetricTimer = new Timer(metric.getName());
		}

		resetList.add(SeriesStats.metricsRuntime);
		Timer wholeMetricsTimer = TimerMap.get(SeriesStats.metricsRuntime);
		if (wholeMetricsTimer == null) {
			wholeMetricsTimer = new Timer(SeriesStats.metricsRuntime);
		}

		singleMetricTimer.restart();
		wholeMetricsTimer.restart();
		boolean res = proceed(metric, u);
		singleMetricTimer.end();
		wholeMetricsTimer.end();
		TimerMap.put(singleMetricTimer);
		TimerMap.put(wholeMetricsTimer);
		return res;
	}
}
