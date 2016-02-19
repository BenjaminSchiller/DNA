package dna.series;

import java.io.File;
import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.labels.Label;
import dna.labels.labeler.Labeler;
import dna.labels.labeler.LabelerNotApplicableException;
import dna.metrics.IMetric;
import dna.metrics.MetricNotApplicableException;
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
import dna.series.Series.RandomSeedReset;
import dna.series.aggdata.AggregatedSeries;
import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.series.data.distr.BinnedDistr;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.BinnedLongDistr;
import dna.series.data.distr.Distr;
import dna.series.data.distr.Distr.DistrType;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.series.lists.ValueList;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.batch.BatchSanitizationStats;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.util.ArrayUtils;
import dna.util.Config;
import dna.util.Log;
import dna.util.Memory;

public class SeriesGeneration {

	public static SeriesData generate(Series series, int runs, int batches)
			throws AggregationException, IOException,
			MetricNotApplicableException, LabelerNotApplicableException {
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
	 * @throws LabelerNotApplicableException
	 */
	public static SeriesData generate(Series series, int runs, int batches,
			boolean compare, boolean aggregate, boolean write,
			long batchGenerationTime) throws AggregationException, IOException,
			MetricNotApplicableException, LabelerNotApplicableException {
		Log.infoSep();
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
		if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			Log.info("r  = zipped");
			Log.info("b  = files");
		} else if (Config.get("GENERATION_AS_ZIP").equals("batches")) {
			Log.info("r  = files");
			Log.info("b  = zipped");
		} else {
			Log.info("r  = files");
			Log.info("b  = files");
		}
		Log.info("p  = " + series.getDir());
		if (batchGenerationTime > 0)
			Log.info("t  = " + batchGenerationTime + " msec / batch");
		StringBuffer buff = new StringBuffer("");
		for (IMetric m : series.getMetrics()) {
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

			AggregatedSeries aSd = Aggregation.aggregateSeries(sd);
			sd.setAggregation(aSd);
			// end of aggregation
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
	 * @throws LabelerNotApplicableException
	 */
	public static SeriesData generateRuns(Series series, int from, int to,
			int batches, boolean compare, boolean write,
			long batchGenerationTime) throws IOException,
			MetricNotApplicableException, LabelerNotApplicableException {
		int runs = to - from;

		for (int i = 0; i <= runs; i++) {
			SeriesGeneration.generateRun(series, from + i, batches, compare,
					write, batchGenerationTime);
		}

		// read structure
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

		// return
		return sd;
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
	 * @throws LabelerNotApplicableException
	 */
	public static void generateRun(Series series, int run, int batches,
			boolean compare, boolean write, long batchGenerationTime)
			throws IOException, MetricNotApplicableException,
			LabelerNotApplicableException {

		/*
		 * compile lists of different algorithm types
		 */
		Algorithms algorithms = new Algorithms(series.getMetrics());
		for (IMetric m : series.getMetrics()) {
			m.reset();
		}

		Log.infoSep();
		Log.info("run " + run + " (" + batches + " batches)");

		// set zip flags
		boolean zippedBatches = false;
		boolean zippedRuns = false;

		if (Config.get("GENERATION_AS_ZIP").equals("runs"))
			zippedRuns = true;
		if (Config.get("GENERATION_AS_ZIP").equals("batches"))
			zippedBatches = true;

		// reset batch generator
		series.getBatchGenerator().reset();

		// reset rand per batch
		if (series.getRandomSeedReset() == RandomSeedReset.eachBatch) {
			series.resetRand();
		}

		// check if labellers applicable
		for (Labeler l : series.getLabeller()) {
			if (!l.isApplicable(series.getGraphGenerator(),
					series.getBatchGenerator(), series.getMetrics()))
				throw new LabelerNotApplicableException(l, series);
		}

		// generate initial data
		BatchData initialData = SeriesGeneration.generateInitialData(series,
				algorithms);
		if (compare) {
			SeriesGeneration.compareMetrics(series);
		}
		if (write) {
			initialData.writeIntelligent(Dir.getBatchDataDir(series.getDir(),
					run, initialData.getTimestamp()));
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

			BatchData batchData = SeriesGeneration.generateNextBatch(series,
					algorithms);

			// compute labels
			for (Labeler labeller : series.getLabeller()) {
				for (Label l : labeller.computeLabels(series.getGraph(), null,
						batchData, series.getMetrics())) {
					batchData.getLabels().add(l);
				}
			}

			if (compare) {
				SeriesGeneration.compareMetrics(series);
			}
			if (write) {
				if (batchGenerationTime > 0) {
					// craft dirs
					String actualDir = Dir.getBatchDataDir(series.getDir(),
							run, batchData.getTimestamp());
					String tempDir = actualDir.substring(0,
							actualDir.length() - 1)
							+ Dir.tempSuffix
							+ Dir.delimiter;

					// write, for zipped runs use actual dir
					if (zippedRuns)
						batchData.writeIntelligent(actualDir);
					else
						batchData.writeIntelligent(tempDir);

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

					// adjust dir
					if (zippedBatches) {
						actualDir = actualDir.substring(0,
								actualDir.length() - 1)
								+ Config.get("SUFFIX_ZIP_FILE");
						tempDir = actualDir + Dir.tempSuffix;
					}

					// rename
					if (!zippedRuns) {
						File f1 = new File(tempDir);
						File f2 = new File(actualDir);
						if (f1.exists()) {
							if (f2.exists())
								Files.delete(f2);
							f1.renameTo(f2);
						}
					}
				} else {
					// write
					batchData.writeIntelligent(Dir.getBatchDataDir(
							series.getDir(), run, batchData.getTimestamp()));
				}
			}

			// call garbage collection
			if (series.isCallGC() && i == series.getGcOccurence() * gcCounter) {
				System.gc();
				gcCounter++;
			}
		}
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
								IMetric.MetricType.exact)
						|| !series.getMetrics()[j].getMetricType().equals(
								IMetric.MetricType.exact)) {
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

	private static BatchData computeInitialData(Series series,
			Algorithms algorithms) throws MetricNotApplicableException {

		// generate graph
		Log.info("    generating graph");
		series.setGraph(series.getGraphGenerator().generate());
		for (IMetric m : series.getMetrics()) {
			m.setGraph(series.getGraph());
		}

		// generate first batch
		Log.info("    initial data");
		BatchData initialData = new BatchData(series.getGraph().getTimestamp(),
				0, 4, series.getMetrics().length, series.getMetrics().length);
		initialData = computeInitialMetrics(series, initialData, algorithms);

		return initialData;
	}

	private static BatchData computeInitialMetrics(Series series,
			BatchData initialData, Algorithms algorithms)
			throws MetricNotApplicableException {
		// initial computation of all metrics
		for (IMetric m : series.getMetrics()) {
			if (!m.isApplicable(series.getGraph())) {
				throw new MetricNotApplicableException(m, series.getGraph());
			}
			boolean success = false;
			if (m instanceof IDynamicAlgorithm) {
				success = ((IDynamicAlgorithm) m).init();
			} else if (m instanceof IRecomputation) {
				success = ((IRecomputation) m).recompute();
			} else {
				Log.error("unknown metric type: " + m.getClass());
			}
			if (success) {
				// get data
				MetricData data = m.getData();

				// add extra values for distributions
				if (Config.getBoolean("GENERATE_VALUES_FROM_DISTRIBUTION")) {
					for (Distr<?, ?> d : data.getDistributions().getList()) {
						SeriesGeneration.addExtraDistributionValuesToList(d,
								data.getValues(),
								Config.getExtraValueGenerationFlags());
					}
				}
				// add extra values for nodevaluelists
				if (Config.getBoolean("GENERATE_VALUES_FROM_NODEVALUELIST")) {
					for (NodeValueList n : data.getNodeValues().getList()) {
						SeriesGeneration.addExtraNodeValueListValuesToList(n,
								data.getValues());
					}
				}

				// add metric to batch
				initialData.getMetrics().add(data);
			} else {
				Log.error("could not create initial data for metric "
						+ m.getDescription());
			}
		}
		return initialData;
	}

	public static BatchData generateInitialData(Series series,
			Algorithms algorithms) throws MetricNotApplicableException {
		BatchData initialData = computeInitialData(series, algorithms);

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

	private static BatchData computeNextBatch(Batch b, Series series,
			Algorithms algorithms) throws MetricNotApplicableException {
		// check applicability to batch
		for (IMetric m : series.getMetrics()) {
			if (!m.isApplicable(b)) {
				throw new MetricNotApplicableException(m, b);
			}
		}

		// apply before batch
		for (IBeforeBatch m : algorithms.beforeBatch) {
			m.applyBeforeBatch(b);
		}

		BatchSanitizationStats sanitizationStats = BatchSanitization
				.sanitize(b);
		if (sanitizationStats.getTotal() > 0) {
			Log.info("      " + sanitizationStats);
			Log.info("      => " + b.toString());
		}

		int removedNodes = SeriesGeneration.applyNRs(series, algorithms,
				b.getNodeRemovals());
		int removedEdges = SeriesGeneration.applyERs(series, algorithms,
				b.getEdgeRemovals());

		int addedNodes = SeriesGeneration.applyNAs(series, algorithms,
				b.getNodeAdditions());
		int addedEdges = SeriesGeneration.applyEAs(series, algorithms,
				b.getEdgeAdditions());

		int updatedNodeWeights = SeriesGeneration.applyNWs(series, algorithms,
				b.getNodeWeights());
		int updatedEdgeWeights = SeriesGeneration.applyEWs(series, algorithms,
				b.getEdgeWeights());

		// int removedNodes = SeriesGeneration.applyUpdates(series,
		// b.getNodeRemovals(), beforeUpdate, afterUpdate);
		// int removedEdges = SeriesGeneration.applyUpdates(series,
		// b.getEdgeRemovals(), beforeUpdate, afterUpdate);
		//
		// int addedNodes = SeriesGeneration.applyUpdates(series,
		// b.getNodeAdditions(), beforeUpdate, afterUpdate);
		// int addedEdges = SeriesGeneration.applyUpdates(series,
		// b.getEdgeAdditions(), beforeUpdate, afterUpdate);
		//
		// int updatedNodeWeights = SeriesGeneration.applyUpdates(series,
		// b.getNodeWeights(), beforeUpdate, afterUpdate);
		// int updatedEdgeWeights = SeriesGeneration.applyUpdates(series,
		// b.getEdgeWeights(), beforeUpdate, afterUpdate);

		series.getGraph().setTimestamp(b.getTo());

		// apply after batch
		for (IAfterBatch m : algorithms.afterBatch) {
			m.applyAfterBatch(b);
		}

		// compute
		for (IRecomputation m : algorithms.recomputation) {
			m.recompute();
		}

		BatchData batchData = new BatchData(b, sanitizationStats, 5, 5,
				series.getMetrics().length, series.getMetrics().length);

		batchData.getValues()
				.add(new Value(SeriesStats.addedNodes, addedNodes));
		batchData.getValues().add(
				new Value(SeriesStats.removedNodes, removedNodes));
		batchData.getValues().add(
				new Value(SeriesStats.updatedNodeWeights, updatedNodeWeights));
		batchData.getValues()
				.add(new Value(SeriesStats.addedEdges, addedEdges));
		batchData.getValues().add(
				new Value(SeriesStats.removedEdges, removedEdges));
		batchData.getValues().add(
				new Value(SeriesStats.updatedEdgeWeights, updatedEdgeWeights));

		return batchData;
	}

	public static BatchData generateNextBatch(Series series,
			Algorithms algorithms) throws MetricNotApplicableException {
		// generate next batch
		Batch b = series.getBatchGenerator().generate(series.getGraph());
		Log.info("    " + b.toString());

		// compute next batchdata
		BatchData batchData = computeNextBatch(b, series, algorithms);

		// add values
		batchData.getValues().add(
				new Value(SeriesStats.nodesToAdd, batchData.getBatch()
						.getNodeAdditionsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.nodesToRemove, batchData.getBatch()
						.getNodeRemovalsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.nodeWeightsToUpdate, batchData.getBatch()
						.getNodeWeightsCount()));

		batchData.getValues().add(
				new Value(SeriesStats.edgesToAdd, batchData.getBatch()
						.getEdgeAdditionsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.edgesToRemove, batchData.getBatch()
						.getEdgeRemovalsCount()));
		batchData.getValues().add(
				new Value(SeriesStats.edgeWeightsToUpdate, batchData.getBatch()
						.getEdgeWeightsCount()));

		batchData.getValues().add(
				new Value(SeriesStats.deletedNodeAdditions, batchData
						.getSanitizationStats().getDeletedNodeAdditions()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedEdgeAdditions, batchData
						.getSanitizationStats().getDeletedEdgeAdditions()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedNodeRemovals, batchData
						.getSanitizationStats().getDeletedNodeRemovals()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedEdgeRemovals, batchData
						.getSanitizationStats().getDeletedEdgeRemovals()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedNodeWeightUpdates, batchData
						.getSanitizationStats().getDeletedNodeWeights()));
		batchData.getValues().add(
				new Value(SeriesStats.deletedEdgeWeightUpdates, batchData
						.getSanitizationStats().getDeletedEdgeWeights()));

		batchData.getValues().add(
				new Value(SeriesStats.randomSeed, series.getSeed()));

		// release batch
		batchData.releaseBatch();

		// record memory usage
		double mem = (new Memory()).getUsed();
		batchData.getValues().add(new Value(SeriesStats.memory, mem));

		// add nodes/edges count
		batchData.getValues().add(
				new Value(SeriesStats.nodes, series.getGraph().getNodeCount()));
		batchData.getValues().add(
				new Value(SeriesStats.edges, series.getGraph().getEdgeCount()));

		// add metric data
		for (IMetric m : series.getMetrics()) {
			// get data
			MetricData data = m.getData();

			// add extra values for distributions
			if (Config.getBoolean("GENERATE_VALUES_FROM_DISTRIBUTION")) {
				for (Distr<?, ?> d : data.getDistributions().getList()) {
					SeriesGeneration.addExtraDistributionValuesToList(d,
							data.getValues(),
							Config.getExtraValueGenerationFlags());
				}
			}
			// add extra values for nodevaluelists
			if (Config.getBoolean("GENERATE_VALUES_FROM_NODEVALUELIST")) {
				for (NodeValueList n : data.getNodeValues().getList()) {
					SeriesGeneration.addExtraNodeValueListValuesToList(n,
							data.getValues());
				}
			}

			// add metric to batch
			batchData.getMetrics().add(data);
		}

		return batchData;
	}

	private static int applyNAs(Series series, Algorithms algorithms,
			Iterable<NodeAddition> updates) {
		int counter = 0;

		for (NodeAddition u : updates) {
			for (IBeforeNA m : algorithms.beforeUpdateNA) {
				if (!m.applyBeforeUpdate(u)) {
					Log.error("could not apply before update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}

			if (!u.apply(series.getGraph())) {
				Log.error("could not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			for (IAfterNA m : algorithms.afterUpdateNA) {
				if (!m.applyAfterUpdate(u)) {
					Log.error("could not apply after update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}
		}

		return counter;
	}

	private static int applyNRs(Series series, Algorithms algorithms,
			Iterable<NodeRemoval> updates) {
		int counter = 0;

		for (NodeRemoval u : updates) {
			for (IBeforeNR m : algorithms.beforeUpdateNR) {
				if (!m.applyBeforeUpdate(u)) {
					Log.error("could not apply before update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}

			if (!u.apply(series.getGraph())) {
				Log.error("could not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			for (IAfterNR m : algorithms.afterUpdateNR) {
				if (!m.applyAfterUpdate(u)) {
					Log.error("could not apply after update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}
		}

		return counter;
	}

	private static int applyNWs(Series series, Algorithms algorithms,
			Iterable<NodeWeight> updates) {
		int counter = 0;

		for (NodeWeight u : updates) {
			for (IBeforeNW m : algorithms.beforeUpdateNW) {
				if (!m.applyBeforeUpdate(u)) {
					Log.error("could not apply before update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}

			if (!u.apply(series.getGraph())) {
				Log.error("could not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			for (IAfterNW m : algorithms.afterUpdateNW) {
				if (!m.applyAfterUpdate(u)) {
					Log.error("could not apply after update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}
		}

		return counter;
	}

	private static int applyEAs(Series series, Algorithms algorithms,
			Iterable<EdgeAddition> updates) {
		int counter = 0;

		for (EdgeAddition u : updates) {
			for (IBeforeEA m : algorithms.beforeUpdateEA) {
				if (!m.applyBeforeUpdate(u)) {
					Log.error("could not apply before update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}

			if (!u.apply(series.getGraph())) {
				Log.error("could not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			for (IAfterEA m : algorithms.afterUpdateEA) {
				if (!m.applyAfterUpdate(u)) {
					Log.error("could not apply after update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}
		}

		return counter;
	}

	private static int applyERs(Series series, Algorithms algorithms,
			Iterable<EdgeRemoval> updates) {
		int counter = 0;

		for (EdgeRemoval u : updates) {
			for (IBeforeER m : algorithms.beforeUpdateER) {
				if (!m.applyBeforeUpdate(u)) {
					Log.error("could not apply before update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}

			if (!u.apply(series.getGraph())) {
				Log.error("could not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			for (IAfterER m : algorithms.afterUpdateER) {
				if (!m.applyAfterUpdate(u)) {
					Log.error("could not apply after update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}
		}

		return counter;
	}

	private static int applyEWs(Series series, Algorithms algorithms,
			Iterable<EdgeWeight> updates) {
		int counter = 0;

		for (EdgeWeight u : updates) {
			for (IBeforeEW m : algorithms.beforeUpdateEW) {
				if (!m.applyBeforeUpdate(u)) {
					Log.error("could not apply before update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}

			if (!u.apply(series.getGraph())) {
				Log.error("could not apply update " + u.toString()
						+ " (BUT metric before update already applied)");
				continue;
			}
			counter++;

			for (IAfterEW m : algorithms.afterUpdateEW) {
				if (!m.applyAfterUpdate(u)) {
					Log.error("could not apply after update " + u.toString()
							+ " to metric " + m.getDescription());
				}
			}
		}

		return counter;
	}

	/**
	 * Generates extra Distribution values i.E. DD_MAX, DD_AVG, and adds them to
	 * the ValueList.
	 **/
	private static void addExtraDistributionValuesToList(Distr<?, ?> d1,
			ValueList values, boolean[] flags) {

		DistrType type = d1.getDistrType();

		if (type.equals(DistrType.BINNED_DOUBLE)
				|| type.equals(DistrType.BINNED_INT)
				|| type.equals(DistrType.BINNED_LONG)) {
			BinnedDistr<?> d = (BinnedDistr<?>) d1;

			String name = d.getName();
			long[] valuesArray = d.getValues();

			// add values
			if (flags[0])
				values.add(new Value(name + "_MIN", ArrayUtils.min(valuesArray)));
			if (flags[1])
				values.add(new Value(name + "_MAX", ArrayUtils.min(valuesArray)));
			if (flags[2])
				values.add(new Value(name + "_MED", ArrayUtils.med(valuesArray)));
			if (flags[3])
				values.add(new Value(name + "_AVG", ArrayUtils.avg(valuesArray)));
			if (flags[4])
				values.add(new Value(name + "_DENOMINATOR", d.getDenominator()));
			if (flags[5]) {
				if (type.equals(DistrType.BINNED_DOUBLE))
					values.add(new Value(name + "_BINSIZE",
							((BinnedDoubleDistr) d).getBinSize()));

				if (type.equals(DistrType.BINNED_INT))
					values.add(new Value(name + "_BINSIZE",
							((BinnedIntDistr) d).getBinSize()));

				if (type.equals(DistrType.BINNED_LONG))
					values.add(new Value(name + "_BINSIZE",
							((BinnedLongDistr) d).getBinSize()));
			}
		}
	}

	/**
	 * Generates extra NodeValueList values i.E. localCC_MAX, localCC_AVG, and
	 * adds them to the ValueList.
	 **/
	private static void addExtraNodeValueListValuesToList(NodeValueList n,
			ValueList values) {
		double val = 0;
		if (Config.getBoolean("GENERATE_NODEVALUELIST_MIN")) {
			if (n.getValues().length != 0)
				val = ArrayUtils.min(n.getValues());
			Value v = new Value(n.getName() + "_MIN", val);
			values.add(v);
		}
		if (Config.getBoolean("GENERATE_NODEVALUELIST_MAX")) {
			if (n.getValues().length != 0)
				val = ArrayUtils.max(n.getValues());
			Value v = new Value(n.getName() + "_MAX", val);
			values.add(v);
		}
		if (Config.getBoolean("GENERATE_NODEVALUELIST_MED")) {
			if (n.getValues().length != 0)
				val = ArrayUtils.med(n.getValues());
			Value v = new Value(n.getName() + "_MED", val);
			values.add(v);
		}
		if (Config.getBoolean("GENERATE_NODEVALUELIST_AVG")) {
			if (n.getValues().length != 0)
				val = ArrayUtils.avg(n.getValues());
			Value v = new Value(n.getName() + "_AVG", val);
			values.add(v);
		}
	}

	// private static int applyUpdates(Series series,
	// Iterable<? extends Update> updates,
	// IBeforeUpdateWeighted[] beforeUpdate,
	// IAfterUpdateWeighted[] afterUpdate) {
	//
	// int counter = 0;
	// for (Update u : updates) {
	//
	// // apply update to metrics beforehand
	// for (IBeforeUpdateWeighted m : beforeUpdate) {
	// boolean success = false;
	// if (u instanceof NodeAddition) {
	// success = m.applyBeforeUpdate((NodeAddition) u);
	// } else if (u instanceof NodeRemoval) {
	// success = m.applyBeforeUpdate((NodeRemoval) u);
	// } else if (u instanceof EdgeAddition) {
	// success = m.applyBeforeUpdate((EdgeAddition) u);
	// } else if (u instanceof EdgeRemoval) {
	// success = m.applyBeforeUpdate((EdgeRemoval) u);
	// } else if (u instanceof NodeWeight) {
	// success = m.applyBeforeUpdate((NodeWeight) u);
	// } else if (u instanceof EdgeWeight) {
	// success = m.applyBeforeUpdate((EdgeWeight) u);
	// } else {
	// Log.error("unknown update type: " + u.getClass());
	// }
	// if (!success) {
	// Log.error("could not apply before update " + u.toString()
	// + " to metric " + m.getDescription());
	// }
	// }
	//
	// if (!u.apply(series.getGraph())) {
	// Log.error("could not apply update " + u.toString()
	// + " (BUT metric before update already applied)");
	// continue;
	// }
	// counter++;
	//
	// // apply update to metrics afterwards
	// for (IAfterUpdateWeighted m : afterUpdate) {
	// boolean success = false;
	// if (u instanceof NodeAddition) {
	// success = m.applyAfterUpdate((NodeAddition) u);
	// } else if (u instanceof NodeRemoval) {
	// success = m.applyAfterUpdate((NodeRemoval) u);
	// } else if (u instanceof EdgeAddition) {
	// success = m.applyAfterUpdate((EdgeAddition) u);
	// } else if (u instanceof EdgeRemoval) {
	// success = m.applyAfterUpdate((EdgeRemoval) u);
	// } else if (u instanceof NodeWeight) {
	// success = m.applyAfterUpdate((NodeWeight) u);
	// } else if (u instanceof EdgeWeight) {
	// success = m.applyAfterUpdate((EdgeWeight) u);
	// } else {
	// Log.error("unknown update type: " + u.getClass());
	// }
	// if (!success) {
	// Log.error("could not apply after update " + u.toString()
	// + " to metric " + m.getDescription());
	// }
	// }
	// }
	//
	// return counter;
	//
	// }

}
