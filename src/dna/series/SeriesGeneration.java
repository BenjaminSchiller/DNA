package dna.series;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.HashMap;

import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.metrics.Metric;
import dna.metrics.Metric.MetricType;
import dna.metrics.MetricNotApplicableException;
import dna.series.Series.RandomSeedReset;
import dna.series.aggdata.AggregatedSeries;
import dna.series.data.BatchData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.batch.BatchSanitizationStats;
import dna.updates.update.Update;
import dna.util.Config;
import dna.util.Log;
import dna.util.Memory;
import dna.util.Timer;

public class SeriesGeneration {

	public static FileSystem writeFileSystem;
	public static FileSystem readFileSystem;

	public static SeriesData generate(Series series, int runs, int batches)
			throws AggregationException, IOException,
			MetricNotApplicableException {
		return SeriesGeneration.generate(series, runs, batches, true, true,
				true, 0);
	}

	/**
	 * Generates a SeriesData object for a given series.
	 * 
	 * @param series
	 *            Series for which the SeriesData object will be generated
	 * @param runs
	 *            Amount of runs to be generated
	 * @param batches
	 *            Amount of badges to be generated
	 * @param compare
	 *            Flag that decides whether metrics will be automatically
	 *            compared or not
	 * @param aggregate
	 *            Flag that decides whether data will be aggregated or not
	 * @param write
	 *            Flag that decides whether data will be written on the
	 *            filesystem or not
	 * @param batchGenerationTime
	 *            Long variable representing the artificial generation-time for
	 *            each batch. Used to simulate a live system.
	 * @return SeriesData object representing the given series
	 * @throws AggregationException
	 * @throws IOException
	 * @throws MetricNotApplicableException
	 */
	public static SeriesData generate(Series series, int runs, int batches,
			boolean compare, boolean aggregate, boolean write,
			long batchGenerationTime) throws AggregationException, IOException,
			MetricNotApplicableException {
		Log.infoSep();
		Timer timer = new Timer("seriesGeneration");
		Log.info("generating series");
		Log.infoSep();
		Log.info("ds = "
				+ series.getGraphGenerator().getGraphDataStructure()
						.getStorageDataStructures(true));
		Log.info("gg = " + series.getGraphGenerator().getDescription());
		Log.info("bg = " + series.getBatchGenerator().getDescription());
		if (aggregate)
			Log.info("ag = enabled");
		else
			Log.info("ag = disabled");
		if (Config.getBoolean("GENERATION_BATCHES_AS_ZIP"))
			Log.info("b  = zipped");
		else
			Log.info("b  = files");
		Log.info("p  = " + series.getDir());
		if (batchGenerationTime > 0)
			Log.info("t  = " + batchGenerationTime + " msec / batch");
		StringBuffer buff = new StringBuffer("");
		for (Metric m : series.getMetrics()) {
			if (buff.length() > 0) {
				buff.append("\n     ");
			}
			buff.append(m.getDescription());
		}
		Log.info("m  = " + buff.toString());

		// reset rand per series
		if (series.getRandomSeedReset() == RandomSeedReset.eachSeries) {
			series.resetRand();
		}

		// generate all runs
		for (int r = 0; r < runs; r++) {
			// reset rand per batch / run
			if (series.getRandomSeedReset() == RandomSeedReset.eachRun) {
				series.resetRand();
			}

			// generate runW
			SeriesGeneration.generateRun(series, r, batches, compare, write,
					batchGenerationTime);
		}

		// read series data structure for aggregation
		SeriesData sd = SeriesData.read(series.getDir(), series.getName(),
				false, false);

		// compare metrics
		if (compare) {
			try {
				sd.compareMetrics(true);
			} catch (InterruptedException e) {
				Log.warn("Error on comparing metrics");
			}
		}
		// aggregate all runs
		if (aggregate) {
			Log.infoSep();
			Timer aggregationTimer = new Timer("aggregation");

			AggregatedSeries aSd = Aggregation.aggregateSeries(sd);
			sd.setAggregation(aSd);
			aggregationTimer.end();
			Log.info(aggregationTimer.toString());
			// end of aggregation
			Log.infoSep();
			timer.end();
			Log.info("total time: " + timer.toString());
			Log.infoSep();
		}
		return sd;
	}

	/**
	 * Generates seperated runs for a series. Parameters 'from' and 'to' mark
	 * the range. Example: generateRuns(5, 8) -> generates run.5, run.6,
	 * run.7,run.8
	 * 
	 * @param series
	 *            Series for which the runs will be generated
	 * @param from
	 *            Index of the first run
	 * @param to
	 *            Index of the last run
	 * @param batches
	 *            Amount of batches that will be generated
	 * @param compare
	 *            Flag that decides whether metrics will be automatically
	 *            compared or not
	 * @param write
	 *            Flag that decides whether data will be written on the
	 *            filesystem or not
	 * @param batchGenerationTime
	 *            Long variable representing the artificial generation-time for
	 *            each batch. Used to simulate a live system.
	 * @return RunDataList object containing the generated runs
	 * @throws IOException
	 * @throws MetricNotApplicableException
	 */
	public static void generateRuns(Series series, int from, int to,
			int batches, boolean compare, boolean write,
			long batchGenerationTime) throws IOException,
			MetricNotApplicableException {
		int runs = to - from;

		for (int i = 0; i < runs; i++) {
			SeriesGeneration.generateRun(series, from + i, batches, compare,
					write, batchGenerationTime);
		}
	}

	/**
	 * Generates one run of a given series
	 * 
	 * @param series
	 *            Series for which the runs will be generated
	 * @param run
	 *            Index of the generated run
	 * @param batches
	 *            Amount of batches that will be generated
	 * @param compare
	 *            Flag that decides whether metrics will be automatically
	 *            compared or not
	 * @param write
	 *            Flag that decides whether data will be written on the
	 *            filesystem or not
	 * @param batchGenerationTime
	 *            Long variable representing the artificial generation-time for
	 *            each batch. Used to simulate a live system.
	 * @return RunData object representing the generated run
	 * @throws IOException
	 * @throws MetricNotApplicableException
	 */
	public static void generateRun(Series series, int run, int batches,
			boolean compare, boolean write, long batchGenerationTime)
			throws IOException, MetricNotApplicableException {

		Log.infoSep();
		Timer timer = new Timer("runGeneration");
		Log.info("run " + run + " (" + batches + " batches)");

		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");

		// reset batch generator
		series.getBatchGenerator().reset();

		// reset rand per batch
		if (series.getRandomSeedReset() == RandomSeedReset.eachBatch) {
			series.resetRand();
		}

		// generate initial data
		BatchData initialData = SeriesGeneration.generateInitialData(series);
		if (compare) {
			SeriesGeneration.compareMetrics(series);
		}
		if (write) {
			if (!singleFile) {
				initialData.write(Dir.getBatchDataDir(series.getDir(), run,
						initialData.getTimestamp()));
			} else {
				try {
					initialData.writeSingleFile(
							Dir.getRunDataDir(series.getDir(), run),
							initialData.getTimestamp(),
							Config.get("SUFFIX_ZIP_FILE"), Dir.delimiter);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// garbage collection counter
		int gcCounter = 1;

		// generate batch data
		for (int i = 0; i < batches; i++) {
			if (!series.getBatchGenerator().isFurtherBatchPossible(
					series.getGraph())) {
				Log.info("    no further batch possible (generated " + i
						+ " of " + batches + ")");
				break;
			}
			// * live display simulation
			long batchGenerationStart = System.currentTimeMillis();
			// *

			// reset rand per batch
			if (series.getRandomSeedReset() == RandomSeedReset.eachBatch) {
				series.resetRand();
			}

			BatchData batchData = SeriesGeneration.generateNextBatch(series);

			if (compare) {
				SeriesGeneration.compareMetrics(series);
			}
			if (write) {
				if (batchGenerationTime > 0) {
					// generation simulation
					String actualDir;
					String dirTemp;

					if (singleFile) {
						String nonZipDir = Dir.getBatchDataDir(series.getDir(),
								run, batchData.getTimestamp());
						actualDir = nonZipDir.substring(0,
								nonZipDir.length() - 1)
								+ Config.get("SUFFIX_ZIP_FILE");
						dirTemp = actualDir + Dir.tempSuffix;
					} else {
						actualDir = Dir.getBatchDataDir(series.getDir(), run,
								batchData.getTimestamp());
						dirTemp = actualDir
								.substring(0, actualDir.length() - 1)
								+ Dir.tempSuffix + Dir.delimiter;
					}

					// rename directory
					File srcDir = new File(dirTemp);
					File dstDir = new File(actualDir);

					Files.delete(srcDir);
					Files.delete(dstDir);

					// write
					if (singleFile)
						batchData.writeSingleFile(
								Dir.getRunDataDir(series.getDir(), run),
								batchData.getTimestamp(),
								Config.get("SUFFIX_ZIP_FILE") + Dir.tempSuffix,
								Dir.delimiter);
					else
						batchData.write(dirTemp);

					// live display simulation
					long waitTime = batchGenerationTime
							- (System.currentTimeMillis() - batchGenerationStart);
					if (waitTime > 0) {
						try {
							Thread.sleep(waitTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// rename
					if (srcDir.exists())
						srcDir.renameTo(dstDir);
				} else {
					// no generation simulation
					if (singleFile)
						batchData.writeSingleFile(
								Dir.getRunDataDir(series.getDir(), run),
								batchData.getTimestamp(),
								Config.get("SUFFIX_ZIP_FILE"), Dir.delimiter);
					else
						batchData.write(Dir.getBatchDataDir(series.getDir(),
								run, batchData.getTimestamp()));
				}
			}

			// call garbage collection
			if (series.isCallGC() && i == series.getGcOccurence() * gcCounter) {
				System.gc();
				gcCounter++;
			}
		}

		timer.end();
		Log.info(timer.toString());
	}

	private static boolean compareMetrics(Series series) {
		boolean ok = true;
		for (int i = 0; i < series.getMetrics().length; i++) {
			for (int j = i + 1; j < series.getMetrics().length; j++) {
				if (i == j) {
					continue;
				}
				if (!series.getMetrics()[i]
						.isComparableTo(series.getMetrics()[j])
						|| !series.getMetrics()[i].getMetricType().equals(
								MetricType.exact)
						|| !series.getMetrics()[j].getMetricType().equals(
								MetricType.exact)) {
					continue;
				}
				if (!series.getMetrics()[i].equals(series.getMetrics()[j])) {
					Log.error(series.getMetrics()[i].getDescription() + " != "
							+ series.getMetrics()[j].getDescription());
					ok = false;
				}
			}
		}
		return ok;
	}

	public static BatchData generateInitialData(Series series)
			throws MetricNotApplicableException {

		Timer totalTimer = new Timer("total");

		Log.info("    inital data");

		// generate graph
		Timer graphGenerationTimer = new Timer("graphGeneration");
		series.setGraph(series.getGraphGenerator().generate());
		graphGenerationTimer.end();
		for (Metric m : series.getMetrics()) {
			m.setGraph(series.getGraph());
		}

		// initialize data
		BatchData initialData = new BatchData(series.getGraph().getTimestamp(),
				0, 4, series.getMetrics().length, series.getMetrics().length);

		// initial computation of all metrics
		Timer allMetricsTimer = new Timer("metrics");
		for (Metric m : series.getMetrics()) {
			Timer metricTimer = new Timer(m.getName());
			if (!m.isApplicable(series.getGraph())) {
				throw new MetricNotApplicableException(m, series.getGraph());
			}
			m.init();
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
		// batchGeneration runtime is not present in the initialdata and added
		// for gui purposes only
		initialData.getGeneralRuntimes().add(
				new RunTime("batchGeneration", 0.0));

		addSummaryRuntimes(initialData);

		// add values
		initialData.getValues().add(new Value("randomSeed", series.getSeed()));

		// these values are not present in the initialdata and added for gui
		// purposes only
		initialData.getValues().add(new Value(SeriesStats.nodesToAdd, 0.0));
		initialData.getValues().add(new Value(SeriesStats.addedNodes, 0.0));
		initialData.getValues().add(new Value(SeriesStats.nodesToRemove, 0.0));
		initialData.getValues().add(new Value(SeriesStats.removedNodes, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.nodeWeightsToUpdate, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.updatedNodeWeights, 0.0));

		initialData.getValues().add(new Value(SeriesStats.edgesToAdd, 0.0));
		initialData.getValues().add(new Value(SeriesStats.addedEdges, 0.0));
		initialData.getValues().add(new Value(SeriesStats.edgesToRemove, 0.0));
		initialData.getValues().add(new Value(SeriesStats.removedEdges, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.edgeWeightsToUpdate, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.updatedEdgeWeights, 0.0));

		initialData.getValues().add(
				new Value(SeriesStats.deletedNodeAdditions, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.deletedEdgeAdditions, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.deletedNodeRemovals, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.deletedEdgeRemovals, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.deletedNodeWeightUpdates, 0.0));
		initialData.getValues().add(
				new Value(SeriesStats.deletedEdgeWeightUpdates, 0.0));

		// call garbage collection
		if (series.isCallGC()) {
			System.gc();
		}
		// record memory usage
		double mem = (new Memory()).getUsed();
		initialData.getValues().add(new Value(SeriesStats.memory, mem));
		initialData.getValues().add(
				new Value(SeriesStats.nodes, series.getGraph().getNodeCount()));
		initialData.getValues().add(
				new Value(SeriesStats.edges, series.getGraph().getEdgeCount()));

		return initialData;

	}

	public static BatchData generateNextBatch(Series series)
			throws MetricNotApplicableException {

		int addedNodes = 0;
		int removedNodes = 0;
		int updatedNodeWeights = 0;
		int addedEdges = 0;
		int removedEdges = 0;
		int updatedEdgeWeights = 0;

		Timer totalTimer = new Timer(SeriesStats.totalRuntime);

		Timer batchGenerationTimer = new Timer(
				SeriesStats.batchGenerationRuntime);
		Batch b = series.getBatchGenerator().generate(series.getGraph());
		batchGenerationTimer.end();

		Log.info("    " + b.toString());

		BatchData batchData = new BatchData(b.getTo(), 5, 5,
				series.getMetrics().length, series.getMetrics().length);

		Timer graphUpdateTimer = new Timer(SeriesStats.graphUpdateRuntime);

		// init metric timers
		HashMap<Metric, Timer> timer = new HashMap<Metric, Timer>();
		for (Metric m : series.getMetrics()) {
			if (!m.isApplicable(b)) {
				throw new MetricNotApplicableException(m, b);
			}
			Timer t = new Timer(m.getName());
			t.end();
			timer.put(m, t);
		}
		Timer metricsTotal = new Timer(SeriesStats.metricsRuntime);
		metricsTotal.end();

		// apply before batch
		metricsTotal.restart();
		for (Metric m : series.getMetrics()) {
			if (m.isAppliedBeforeBatch()) {
				timer.get(m).restart();
				m.applyBeforeBatch(b);
				timer.get(m).end();
			}
		}
		metricsTotal.end();

		BatchSanitizationStats sanitizationStats = BatchSanitization
				.sanitize(b);
		if (sanitizationStats.getTotal() > 0) {
			Log.info("      " + sanitizationStats);
			Log.info("      => " + b.toString());
		}

		SeriesGeneration.applyUpdates(series, b.getNodeRemovals(),
				graphUpdateTimer, metricsTotal, timer);
		SeriesGeneration.applyUpdates(series, b.getEdgeRemovals(),
				graphUpdateTimer, metricsTotal, timer);

		SeriesGeneration.applyUpdates(series, b.getNodeAdditions(),
				graphUpdateTimer, metricsTotal, timer);
		SeriesGeneration.applyUpdates(series, b.getEdgeAdditions(),
				graphUpdateTimer, metricsTotal, timer);

		SeriesGeneration.applyUpdates(series, b.getNodeWeights(),
				graphUpdateTimer, metricsTotal, timer);
		SeriesGeneration.applyUpdates(series, b.getEdgeWeights(),
				graphUpdateTimer, metricsTotal, timer);

		series.getGraph().setTimestamp(b.getTo());

		// apply after batch
		metricsTotal.restart();
		for (Metric m : series.getMetrics()) {
			if (m.isAppliedAfterBatch()) {
				timer.get(m).restart();
				m.applyAfterBatch(b);
				timer.get(m).end();
			}
		}
		metricsTotal.end();

		// compute / cleanup
		metricsTotal.restart();
		for (Metric m : series.getMetrics()) {
			if (m.isRecomputed()) {
				timer.get(m).restart();
				m.init();
				m.compute();
				timer.get(m).end();
			}
		}
		metricsTotal.end();

		totalTimer.end();

		// add values
		batchData.getValues().add(
				new Value(SeriesStats.nodesToAdd, b.getNodeAdditionsCount()));
		batchData.getValues()
				.add(new Value(SeriesStats.addedNodes, addedNodes));
		batchData.getValues().add(
				new Value(SeriesStats.nodesToRemove, b.getNodeRemovalsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.removedNodes, removedNodes));
		batchData.getValues().add(
				new Value(SeriesStats.nodeWeightsToUpdate, b
						.getNodeWeightsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.updatedNodeWeights, updatedNodeWeights));

		batchData.getValues().add(
				new Value(SeriesStats.edgesToAdd, b.getEdgeAdditionsCount()));
		batchData.getValues()
				.add(new Value(SeriesStats.addedEdges, addedEdges));
		batchData.getValues().add(
				new Value(SeriesStats.edgesToRemove, b.getEdgeRemovalsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.removedEdges, removedEdges));
		batchData.getValues().add(
				new Value(SeriesStats.edgeWeightsToUpdate, b
						.getEdgeWeightsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.updatedEdgeWeights, updatedEdgeWeights));

		batchData.getValues().add(
				new Value(SeriesStats.deletedNodeAdditions, sanitizationStats
						.getDeletedNodeAdditions()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedEdgeAdditions, sanitizationStats
						.getDeletedEdgeAdditions()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedNodeRemovals, sanitizationStats
						.getDeletedNodeRemovals()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedEdgeRemovals, sanitizationStats
						.getDeletedEdgeRemovals()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedNodeWeightUpdates,
						sanitizationStats.getDeletedNodeWeights()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedEdgeWeightUpdates,
						sanitizationStats.getDeletedEdgeWeights()));

		batchData.getValues().add(
				new Value(SeriesStats.randomSeed, series.getSeed()));

		// release batch
		b = null;

		// record memory usage
		double mem = (new Memory()).getUsed();
		batchData.getValues().add(new Value(SeriesStats.memory, mem));

		// add nodes/edges count
		batchData.getValues().add(
				new Value(SeriesStats.nodes, series.getGraph().getNodeCount()));
		batchData.getValues().add(
				new Value(SeriesStats.edges, series.getGraph().getEdgeCount()));

		// add metric data
		for (Metric m : series.getMetrics()) {
			batchData.getMetrics().add(m.getData());
		}

		// add metric runtimes
		for (Metric m : series.getMetrics()) {
			batchData.getMetricRuntimes().add(timer.get(m).getRuntime());
		}

		// add general runtimes
		batchData.getGeneralRuntimes().add(totalTimer.getRuntime());
		batchData.getGeneralRuntimes().add(batchGenerationTimer.getRuntime());
		batchData.getGeneralRuntimes().add(graphUpdateTimer.getRuntime());
		batchData.getGeneralRuntimes().add(metricsTotal.getRuntime());
		addSummaryRuntimes(batchData);

		return batchData;

	}

	private static int applyUpdates(Series series,
			Iterable<? extends Update> updates, Timer graphUpdateTimer,
			Timer metricsTotal, HashMap<Metric, Timer> timer) {

		int counter = 0;
		for (Update u : updates) {

			// apply update to metrics beforehand
			metricsTotal.restart();
			for (Metric m : series.getMetrics()) {
				if (m.isAppliedBeforeUpdate()) {
					timer.get(m).restart();
					m.applyBeforeUpdate(u);
					timer.get(m).end();
				}
			}
			metricsTotal.end();

			// update graph datastructures
			graphUpdateTimer.restart();

			boolean success = u.apply(series.getGraph());
			graphUpdateTimer.end();
			if (!success) {
				Log.error("could not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			// apply update to metrics afterwards
			metricsTotal.restart();
			for (Metric m : series.getMetrics()) {
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

	private static void addSummaryRuntimes(BatchData batchData) {
		double total = batchData.getGeneralRuntimes().get("total").getRuntime();
		double metrics = batchData.getGeneralRuntimes().get("metrics")
				.getRuntime();
		double sum = sumRuntimes(batchData) - total - metrics;
		double overhead = total - sum;
		batchData.getGeneralRuntimes().add(new RunTime("sum", sum));
		batchData.getGeneralRuntimes().add(new RunTime("overhead", overhead));
	}

	private static long sumRuntimes(BatchData batchData) {
		long sum = 0;
		for (RunTime rt : batchData.getGeneralRuntimes().getList()) {
			sum += rt.getRuntime();
		}
		for (RunTime rt : batchData.getMetricRuntimes().getList()) {
			sum += rt.getRuntime();
		}
		return sum;
	}

}
