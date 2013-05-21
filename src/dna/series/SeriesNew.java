package dna.series;

import java.io.IOException;
import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.GraphGenerator;
import dna.io.filesystem.Dir;
import dna.metrics.MetricNew;
import dna.series.data.DiffData;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.updates.Batch;
import dna.updates.BatchGenerator;
import dna.updates.Update;
import dna.util.Log;
import dna.util.Rand;
import dna.util.Timer;

@SuppressWarnings("rawtypes")
public class SeriesNew {

	public SeriesNew(GraphGenerator gg, BatchGenerator bg, MetricNew[] metrics,
			String dir) {
		this.gg = gg;
		this.bg = bg;
		this.metrics = metrics;
		this.dir = dir;
	}

	public SeriesData generate(int runs, int diffs)
			throws AggregationException, IOException {

		Log.infoSep();
		Timer timer = new Timer("seriesGeneration");
		Log.info("generating series");
		Log.infoSep();
		Log.info("gg = " + this.gg.getDescription());
		Log.info("bg = " + this.bg.getDescription());
		Log.info("p  = " + this.dir);
		StringBuffer buff = new StringBuffer("");
		for (MetricNew m : this.metrics) {
			if (buff.length() > 0) {
				buff.append("\n     ");
			}
			buff.append(m.getDescription());
		}
		Log.info("m  = " + buff.toString());

		SeriesData sd = new SeriesData(this.dir, runs);

		// generate all runs
		for (int r = 0; r < runs; r++) {
			sd.addRun(this.generateRun(r, diffs));
		}

		// aggregate all runs
		RunData aggregation = Aggregation.aggregate(sd);
		sd.setAggregation(aggregation);
		aggregation.write(Dir.getAggregationDataDir(dir));

		Log.infoSep();
		timer.end();
		Log.info("total time: " + timer.toString());
		Log.infoSep();

		return sd;
	}

	public RunData generateRun(int run, int batches) throws IOException {

		Log.infoSep();
		Timer timer = new Timer("runGeneration");
		Log.info("run " + run + " (" + batches + " batches)");

		RunData rd = new RunData(run, batches + 1);

		// generate initial data
		DiffData initialData = this.generateInitialData();
		rd.getDiffs().add(initialData);
		initialData.write(Dir.getDiffDataDir(this.dir, run,
				initialData.getTimestamp()));

		// generate diff data
		for (int i = 0; i < batches; i++) {
			DiffData diffData = this.generateNextDiff(i + 1);
			this.compareMetrics();
			rd.getDiffs().add(diffData);
			diffData.write(Dir.getDiffDataDir(this.dir, run,
					diffData.getTimestamp()));
		}

		timer.end();
		Log.info(timer.toString());

		return rd;

	}

	private boolean compareMetrics() {
		boolean ok = true;
		for (int i = 0; i < this.metrics.length; i++) {
			for (int j = 0; j < this.metrics.length; j++) {
				if (i == j) {
					continue;
				}
				if (!this.metrics[i].isComparableTo(this.metrics[j])) {
					continue;
				}
				if (!this.metrics[i].equals(this.metrics[j])) {
					Log.error(this.metrics[i].getDescription() + " != "
							+ this.metrics[j].getDescription());
					ok = false;
				}
			}
		}
		return ok;
	}

	public DiffData generateInitialData() {

		Timer totalTimer = new Timer("total");

		Log.info("    inital data");

		long seed = System.currentTimeMillis();
		// seed = 0;
		Rand.init(seed);

		// generate graph
		Timer graphGenerationTimer = new Timer("graphGeneration");
		this.g = this.gg.generate();
		graphGenerationTimer.end();
		for (MetricNew m : this.metrics) {
			m.setGraph(this.g);
		}

		// initialize data
		DiffData initialData = new DiffData(this.g.getTimestamp(), 0, 4,
				this.metrics.length, this.metrics.length);

		// initial computation of all metrics
		Timer allMetricsTimer = new Timer("metrics");
		for (MetricNew m : metrics) {
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
		addSummaryRuntimes(initialData);

		// add values
		initialData.getValues().add(new Value("randomSeed", seed));

		return initialData;

	}

	@SuppressWarnings("unchecked")
	public DiffData generateNextDiff(long timestamp) {

		long seed = System.currentTimeMillis();
		// seed = 0;
		Rand.init(seed);

		int addedNodes = 0;
		int removedNodes = 0;
		int updatedNodeWeights = 0;
		int addedEdges = 0;
		int removedEdges = 0;
		int updatedEdgeWeights = 0;

		Timer totalTimer = new Timer("total");

		Timer diffGenerationTimer = new Timer("diffGeneration");
		Batch b = this.bg.generate(this.g);
		diffGenerationTimer.end();

		Log.info("    " + b.toString());

		DiffData diffData = new DiffData(b.getTo(), 5, 5, metrics.length,
				metrics.length);

		Timer graphUpdateTimer = new Timer("graphUpdate");

		// init metric timers
		HashMap<MetricNew, Timer> timer = new HashMap<MetricNew, Timer>();
		for (MetricNew m : this.metrics) {
			Timer t = new Timer(m.getName());
			t.end();
			timer.put(m, t);
		}
		Timer metricsTotal = new Timer("metrics");
		metricsTotal.end();

		// apply before diff
		metricsTotal.restart();
		for (MetricNew m : this.metrics) {
			if (m.isAppliedBeforeBatch()) {
				timer.get(m).restart();
				m.applyBeforeBatch(b);
				timer.get(m).end();
			}
		}
		metricsTotal.end();

		this.applyUpdates(b.getEdgeRemovals(), graphUpdateTimer, metricsTotal,
				timer);
		this.applyUpdates(b.getNodeRemovals(), graphUpdateTimer, metricsTotal,
				timer);

		this.applyUpdates(b.getNodeAdditions(), graphUpdateTimer, metricsTotal,
				timer);
		this.applyUpdates(b.getEdgeAdditions(), graphUpdateTimer, metricsTotal,
				timer);

		this.applyUpdates(b.getNodeWeightUpdates(), graphUpdateTimer,
				metricsTotal, timer);
		this.applyUpdates(b.getEdgeWeightUpdates(), graphUpdateTimer,
				metricsTotal, timer);

		// TODO replace -1 with to from batch
		this.g.setTimestamp(timestamp);

		// apply after diff
		metricsTotal.restart();
		for (MetricNew m : this.metrics) {
			if (m.isAppliedAfterBatch()) {
				timer.get(m).restart();
				m.applyAfterBatch(b);
				timer.get(m).end();
			}
		}
		metricsTotal.end();

		// compute / cleanup
		metricsTotal.restart();
		for (MetricNew m : this.metrics) {
			timer.get(m).restart();
			if (m.isRecomputed()) {
				m.recompute();
			} else {
				m.cleanup();
			}
			timer.get(m).end();
		}
		metricsTotal.end();

		totalTimer.end();

		// add values
		diffData.getValues().add(
				new Value("nodesToAdd", b.getNodeAdditionCount()));
		diffData.getValues().add(new Value("addedNodes", addedNodes));
		diffData.getValues().add(
				new Value("nodesToRemove", b.getNodeRemovalCount()));
		diffData.getValues().add(new Value("removedNodes", removedNodes));
		diffData.getValues().add(
				new Value("nodeWeightsToUpdate", b.getNodeWeightUpdateCount()));
		diffData.getValues().add(
				new Value("updatedNodeWeights", updatedNodeWeights));

		diffData.getValues().add(
				new Value("edgesToAdd", b.getEdgeAdditionCount()));
		diffData.getValues().add(new Value("addedEdges", addedEdges));
		diffData.getValues().add(
				new Value("edgesToRemove", b.getEdgeRemovalCount()));
		diffData.getValues().add(new Value("removedEdges", removedEdges));
		diffData.getValues().add(
				new Value("edgeWeightsToUpdate", b.getEdgeWeightUpdateCount()));
		diffData.getValues().add(
				new Value("updatedEdgeWeights", updatedEdgeWeights));

		diffData.getValues().add(new Value("randomSeed", seed));

		// add metric data
		for (MetricNew m : this.metrics) {
			diffData.getMetrics().add(m.getData());
		}

		// add metric runtimes
		for (MetricNew m : this.metrics) {
			diffData.getMetricRuntimes().add(timer.get(m).getRuntime());
		}

		// add general runtimes
		diffData.getGeneralRuntimes().add(totalTimer.getRuntime());
		diffData.getGeneralRuntimes().add(diffGenerationTimer.getRuntime());
		diffData.getGeneralRuntimes().add(graphUpdateTimer.getRuntime());
		diffData.getGeneralRuntimes().add(metricsTotal.getRuntime());
		addSummaryRuntimes(diffData);

		return diffData;

	}

	@SuppressWarnings("unchecked")
	private int applyUpdates(Iterable<Update> updates, Timer graphUpdateTimer,
			Timer metricsTotal, HashMap<MetricNew, Timer> timer) {

		int counter = 0;
		for (Update u : updates) {

			// apply update to metrics beforehand
			metricsTotal.restart();
			for (MetricNew m : this.metrics) {
				if (m.isAppliedBeforeUpdate()) {
					timer.get(m).restart();
					m.applyBeforeUpdate(u);
					timer.get(m).end();
				}
			}
			metricsTotal.end();

			// update graph datastructures
			graphUpdateTimer.restart();
			boolean success = u.apply(this.g);
			graphUpdateTimer.end();
			if (!success) {
				Log.error("count not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			// apply update to metrics afterwards
			metricsTotal.restart();
			for (MetricNew m : this.metrics) {
				if (m.isAppliedAfterUpdate()) {
					timer.get(m).restart();
					m.applyAfterUpdate(u);
					timer.get(m).end();
				}
			}
			metricsTotal.end();
		}

		return counter;

	}

	private static void addSummaryRuntimes(DiffData diffData) {
		double total = diffData.getGeneralRuntimes().get("total").getRuntime();
		double metrics = diffData.getGeneralRuntimes().get("metrics")
				.getRuntime();
		double sum = sumRuntimes(diffData) - total - metrics;
		double overhead = total - sum;
		diffData.getGeneralRuntimes().add(new RunTime("sum", sum));
		diffData.getGeneralRuntimes().add(new RunTime("overhead", overhead));
	}

	private static long sumRuntimes(DiffData diffData) {
		long sum = 0;
		for (RunTime rt : diffData.getGeneralRuntimes().getList()) {
			sum += rt.getRuntime();
		}
		for (RunTime rt : diffData.getMetricRuntimes().getList()) {
			sum += rt.getRuntime();
		}
		return sum;
	}

	private GraphGenerator gg;

	private BatchGenerator bg;

	private MetricNew[] metrics;

	private String dir;

	private Graph g;

	public GraphGenerator getGraphGenerator() {
		return this.gg;
	}

	public BatchGenerator getBatchGenerator() {
		return this.bg;
	}

	public MetricNew[] getMetrics() {
		return this.metrics;
	}

	public String getDir() {
		return this.dir;
	}

	public Graph getGraph() {
		return this.g;
	}

}
