package dna.series;

import java.io.IOException;
import java.util.HashMap;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.diff.generator.DiffGenerator;
import dna.graph.old.OldEdge;
import dna.graph.old.OldGraph;
import dna.graph.old.OldGraphGenerator;
import dna.io.filesystem.Dir;
import dna.metrics.Metric;
import dna.series.data.BatchData;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.util.Log;
import dna.util.Rand;
import dna.util.Timer;

public class Series {
	private OldGraph g;

	private OldGraphGenerator gg;

	private DiffGenerator dg;

	private Metric[] metrics;

	private String dir;

	public Series(OldGraphGenerator gg, DiffGenerator dg, Metric[] metrics,
			String path) {
		this.g = null;
		this.gg = gg;
		this.dg = dg;
		this.metrics = metrics;
		this.dir = path;
	}

	public SeriesData generate(int runs, int diffs) throws IOException,
			DiffNotApplicableException, AggregationException {

		Log.infoSep();
		Timer timer = new Timer("seriesGeneration");
		Log.info("generating series");
		Log.infoSep();
		Log.info("gg = " + this.gg.getName());
		Log.info("dg = " + this.dg.getName());
		Log.info("p  = " + this.dir);
		StringBuffer buff = new StringBuffer("");
		for (Metric m : this.metrics) {
			if (buff.length() > 0) {
				buff.append("\n     ");
			}
			buff.append(m.getName());
		}
		Log.info("m  = " + buff.toString());

		SeriesData sd = new SeriesData(this.dir, runs);

		// generate all runs
		for (int r = 0; r < runs; r++) {
			sd.addRun(this.generateRun(r, diffs));
		}

		// aggregate runs
		RunData aggregation = Aggregation.aggregate(sd);
		sd.setAggregation(aggregation);
		aggregation.write(Dir.getAggregationDataDir(dir));

		Log.infoSep();
		timer.end();
		Log.info("total time: " + timer.toString());
		Log.infoSep();

		return sd;
	}

	public RunData generateRun(int run, int diffs) throws IOException,
			DiffNotApplicableException {

		Log.infoSep();
		Timer timer = new Timer("runGeneration");
		Log.info("run " + run + " (" + diffs + " diffs)");

		RunData rd = new RunData(run, diffs + 1);

		// generate initial data
		BatchData initialData = this.generateInitialData();
		rd.getBatches().add(initialData);
		initialData.write(Dir.getBatchDataDir(this.dir, run,
				initialData.getTimestamp()));

		// generate diff data
		for (int i = 0; i < diffs; i++) {
			BatchData diffData = this.generateNextDiff();
			rd.getBatches().add(diffData);
			diffData.write(Dir.getBatchDataDir(this.dir, run,
					diffData.getTimestamp()));
		}

		timer.end();
		Log.info(timer.toString());

		return rd;
	}

	public BatchData generateInitialData() {

		Timer totalTimer = new Timer("total");

		Log.info("    inital data");

		long seed = System.currentTimeMillis();
		// seed = 0;
		Rand.init(seed);

		// generate graph
		Timer graphGenerationTimer = new Timer("graphGeneration");
		this.g = this.gg.generate();
		graphGenerationTimer.end();
		for (Metric m : this.metrics) {
			m.setGraph(this.g);
		}

		// initialize data
		BatchData initialData = new BatchData(this.g.getTimestamp(), 0, 4,
				this.metrics.length, this.metrics.length);

		// initial computation of all metrics
		Timer allMetricsTimer = new Timer("metrics");
		for (Metric m : metrics) {
			Timer metricTimer = new Timer(m.getName());
			m.compute();
			metricTimer.end();
			initialData.getMetrics().add(m.getData());
			initialData.getMetricRuntimes().add(metricTimer.getRuntime());
		}
		allMetricsTimer.end();

		totalTimer.end();

		// add general runtimes
		initialData.getGeneralRuntimes().add(totalTimer.getRuntime());
		initialData.getGeneralRuntimes().add(graphGenerationTimer.getRuntime());
		initialData.getGeneralRuntimes().add(allMetricsTimer.getRuntime());
		Series.addSummaryRuntimes(initialData);

		// add values
		initialData.getValues().add(new Value("randomSeed", seed));

		return initialData;
	}

	public BatchData generateNextDiff() throws DiffNotApplicableException {

		long seed = System.currentTimeMillis();
		// seed = 0;
		Rand.init(seed);

		int addedEdges = 0;
		int removedEdges = 0;

		Timer totalTimer = new Timer("total");

		Timer diffGenerationTimer = new Timer("diffGeneration");
		Diff d = dg.generate(g);
		diffGenerationTimer.end();

		Log.info("    " + d.toString());

		BatchData diffData = new BatchData(d.getTo(), 5, 5, metrics.length,
				metrics.length);

		Timer graphUpdateTimer = new Timer("graphUpdate");

		// init metric timers
		HashMap<Metric, Timer> timer = new HashMap<Metric, Timer>();
		for (Metric m : this.metrics) {
			Timer t = new Timer(m.getName());
			t.end();
			timer.put(m, t);
		}
		Timer metricsTotal = new Timer("metrics");
		metricsTotal.end();

		// apply before diff
		metricsTotal.restart();
		for (Metric m : this.metrics) {
			if (m.isAppliedBeforeDiff()) {
				timer.get(m).restart();
				m.applyBeforeDiff(d);
				timer.get(m).end();
			}
		}
		metricsTotal.end();

		// remove edges
		for (OldEdge e : d.getRemovedEdges()) {
			graphUpdateTimer.restart();
			boolean removed = this.g.removeEdge(e);
			graphUpdateTimer.end();
			if (!removed) {
				continue;
			}
			removedEdges++;
			// apply after edge
			metricsTotal.restart();
			for (Metric m : this.metrics) {
				if (m.isAppliedAfterEdge()) {
					timer.get(m).restart();
					m.applyAfterEdgeRemoval(d, e);
					timer.get(m).end();
				}
			}
			metricsTotal.end();
		}

		// add edges
		for (OldEdge e : d.getAddedEdges()) {
			graphUpdateTimer.restart();
			boolean added = this.g.addEdge(e);
			graphUpdateTimer.end();
			if (!added) {
				continue;
			}
			addedEdges++;
			// apply after edge
			metricsTotal.restart();
			for (Metric m : this.metrics) {
				if (m.isAppliedAfterEdge()) {
					timer.get(m).restart();
					m.applyAfterEdgeAddition(d, e);
					timer.get(m).end();
				}
			}
			metricsTotal.end();
		}

		this.g.setTimestamp(d.getTo());

		// apply after diff
		metricsTotal.restart();
		for (Metric m : this.metrics) {
			if (m.isAppliedAfterDiff()) {
				timer.get(m).restart();
				m.applyAfterDiff(d);
				timer.get(m).end();
			}
		}
		metricsTotal.end();

		// compute / cleanup
		metricsTotal.restart();
		for (Metric m : this.metrics) {
			timer.get(m).restart();
			if (m.isComputed()) {
				m.compute();
			} else {
				m.cleanupApplication();
			}
			timer.get(m).end();
		}
		metricsTotal.end();

		totalTimer.end();

		// add values
		diffData.getValues().add(
				new Value("edgesToAdd", d.getAddedEdges().size()));
		diffData.getValues().add(new Value("addedEdges", addedEdges));
		diffData.getValues().add(
				new Value("edgesToRemove", d.getRemovedEdges().size()));
		diffData.getValues().add(new Value("removedEdges", removedEdges));
		diffData.getValues().add(new Value("randomSeed", seed));

		// add metric data
		for (Metric m : this.metrics) {
			diffData.getMetrics().add(m.getData());
		}

		// add metric runtimes
		for (Metric m : this.metrics) {
			diffData.getMetricRuntimes().add(timer.get(m).getRuntime());
		}

		// add general runtimes
		diffData.getGeneralRuntimes().add(totalTimer.getRuntime());
		diffData.getGeneralRuntimes().add(diffGenerationTimer.getRuntime());
		diffData.getGeneralRuntimes().add(graphUpdateTimer.getRuntime());
		diffData.getGeneralRuntimes().add(metricsTotal.getRuntime());
		Series.addSummaryRuntimes(diffData);

		return diffData;
	}

	private static void addSummaryRuntimes(BatchData diffData) {
		double total = diffData.getGeneralRuntimes().get("total").getRuntime();
		double metrics = diffData.getGeneralRuntimes().get("metrics")
				.getRuntime();
		double sum = Series.sumRuntimes(diffData) - total - metrics;
		double overhead = total - sum;
		diffData.getGeneralRuntimes().add(new RunTime("sum", sum));
		diffData.getGeneralRuntimes().add(new RunTime("overhead", overhead));
	}

	private static long sumRuntimes(BatchData diffData) {
		long sum = 0;
		for (RunTime rt : diffData.getGeneralRuntimes().getList()) {
			sum += rt.getRuntime();
		}
		for (RunTime rt : diffData.getMetricRuntimes().getList()) {
			sum += rt.getRuntime();
		}
		return sum;
	}
}
