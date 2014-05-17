package dna.plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dna.io.Writer;
import dna.io.filesystem.Dir;
import dna.io.filesystem.PlotFilenames;
import dna.plot.Gnuplot.PlotStyle;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotType;
import dna.series.SeriesStats;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedMetricList;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedSeries;
import dna.series.aggdata.AggregatedValue;
import dna.series.aggdata.AggregatedValueList;
import dna.series.data.SeriesData;
import dna.util.Config;
import dna.util.Log;
import dna.util.Memory;

public class Plotting {

	/*
	 * Different plot calls
	 */
	public static void plot(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir);
	}

	public static void plot(SeriesData seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir);
	}

	public static void plot(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotFromTo(
				seriesData,
				dstDir,
				0,
				Long.MAX_VALUE,
				1,
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"),
				Config.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE"),
				Config.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"),
				Config.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"));
	}

	public static void plot(SeriesData seriesData, String dstDir,
			PlotType type, PlotStyle style, DistributionPlotType distPlotType,
			NodeValueListOrderBy sortBy, NodeValueListOrder sortOrder)
			throws IOException, InterruptedException {
		Plotting.plotFromTo(new SeriesData[] { seriesData }, dstDir, 0,
				Long.MAX_VALUE, 1, type, style, distPlotType, sortBy, sortOrder);
	}

	public static void plot(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Plotting.plotFromTo(
				seriesData,
				dstDir,
				0,
				Long.MAX_VALUE,
				1,
				type,
				style,
				Config.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE"),
				Config.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"),
				Config.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"));
	}

	/**
	 * Plots data from the given SeriesData's. Which batches are plotted is
	 * defined by the given parameters.
	 * 
	 * Example:
	 * 
	 * plot(data, dir, 2, 12, 3) - will plot every third batch between 2 and 12
	 * -> batches 2, 5, 8, 11
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory where plots and scripts will be written
	 * @param timestampFrom
	 *            Starting timestamp
	 * @param timestampTo
	 *            Ending timestamp
	 * @param stepSize
	 *            StepSize between batches.
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @param distPlotType
	 *            DistributionPlotType
	 * @param sortBy
	 *            Argument the NodeValueList will be sorted by
	 * @param sortOrder
	 *            Sorting order
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotFromTo(SeriesData[] seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepSize, PlotType type,
			PlotStyle style, DistributionPlotType distPlotType,
			NodeValueListOrderBy sortBy, NodeValueListOrder sortOrder)
			throws IOException, InterruptedException {
		for (int i = 0; i < seriesData.length; i++) {
			seriesData[i].setAggregation(AggregatedSeries.readFromTo(
					seriesData[i].getDir(), seriesData[i].getName() + i + "_"
							+ Config.get("RUN_AGGREGATION"), timestampFrom,
					timestampTo, stepSize, BatchReadMode.readAllValues));
		}

		Log.infoSep();
		Log.info("plotting data from batch " + timestampFrom + " - "
				+ timestampTo + " with stepsize " + stepSize + " for "
				+ seriesData.length + " series to " + dstDir);
		(new File(dstDir)).mkdirs();

		// plot different data from the aggregation data
		Plotting.plotDistributions(seriesData, dstDir);
		Plotting.plotValues(seriesData, dstDir);
		Plotting.plotStatistics(seriesData, dstDir);
		Plotting.plotRuntimes(seriesData, dstDir);
		Plotting.plotNodeValueLists(seriesData, dstDir);
	}

	/**
	 * Main plotting method. Takes a plotting config object which controls the
	 * behaviour.
	 * 
	 * @param seriesData
	 * @param dstDir
	 * @param config
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void plotFromToSeq(SeriesData[] seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		long timestampFrom = config.getTimestampFrom();
		long timestampTo = config.getTimestampTo();
		long stepsize = config.getStepsize();

		PlotType type = config.getPlotType();
		PlotStyle style = config.getPlotStyle();

		String title = seriesData[0].getName();

		ArrayList<String> metRuntimes = config.getMetricRuntimes();

		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");

		Log.infoSep();
		Log.info("plotting data from batch " + timestampFrom + " - "
				+ timestampTo + " with stepsize " + stepsize + " for "
				+ seriesData.length + " series to " + dstDir);
		(new File(dstDir)).mkdirs();

		// gather relevant batches
		String tempDir = Dir.getAggregationDataDir(seriesData[0].getDir());
		String[] batches = Dir.getBatchesFromTo(tempDir, timestampFrom,
				timestampTo, stepsize);
		double timestamps[] = new double[batches.length];
		for (int i = 0; i < batches.length; i++) {
			timestamps[i] = Dir.getTimestamp(batches[i]);
		}

		// read single values
		AggregatedBatch[] batchData = new AggregatedBatch[batches.length];
		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			if (singleFile)
				batchData[i] = AggregatedBatch.readFromSingleFile(tempDir,
						timestamp, Dir.delimiter,
						BatchReadMode.readOnlySingleValues);
			else
				batchData[i] = AggregatedBatch.read(
						Dir.getBatchDataDir(tempDir, timestamp), timestamp,
						BatchReadMode.readOnlySingleValues);
		}

		// list relevant batches
		Log.infoSep();
		Log.info("Plotting batches:");
		for (int i = 0; i < batches.length && i <= 3; i++) {
			Log.info("\t" + batches[i]);
		}
		if (batches.length > 3) {
			Log.info("\t...");
			Log.info("\t" + batches[batches.length - 1]);
		}

		// generate gnuplot script files
		AggregatedBatch initBatch = batchData[0];

		if (config.isPlotStatistics()) {
			// plot statistics
			AggregatedValueList values = initBatch.getValues();
			Plotting.plotStatistics(batchData, timestamps, values, dstDir,
					title, style, type);
		}

		if (config.isPlotRuntimes()) {
			// plot general runtimes
			Plotting.plotGeneralRuntimes(batchData, timestamps, metRuntimes,
					dstDir, title, style, type);

			// plot metric runtimes
			AggregatedRunTimeList metricRuntimes = initBatch
					.getMetricRuntimes();
			Plotting.plotMetricRuntimes(batchData, timestamps, metricRuntimes,
					dstDir, title, style, type);
		}

		if (config.isPlotMetricValues()) {
			// plot metric values
			AggregatedMetricList metrics = initBatch.getMetrics();
			Plotting.plotMetricValues(batchData, timestamps, metrics, dstDir,
					title, style, type);
		}

		double mem1 = new Memory().getUsed();
		Log.infoSep();
		Log.info("Finished first plotting attempt");
		Log.info("\tused memory: " + mem1);
		Log.info("Erasing unsused data");

		// free resources
		batchData = null;
		System.gc();

		double mem2 = new Memory().getUsed();
		Log.info("\tremoved: " + (mem1 - mem2));
		Log.info("\tused memory: " + mem2);

		boolean plotDistributions = config.isPlotDistributions();
		boolean plotNodeValues = config.isPlotNodeValueLists();

		if (plotDistributions || plotNodeValues)
			Plotting.plotDistributionsAndNodeValues(plotDistributions,
					plotNodeValues, initBatch, batches, timestamps, tempDir,
					dstDir, title, style, type);

	}

	/** Plots Distributions and NodeValueLists **/
	private static void plotDistributionsAndNodeValues(
			boolean plotDistributions, boolean plotNodeValues,
			AggregatedBatch initBatch, String[] batches, double[] timestamps,
			String aggrDir, String dstDir, String title, PlotStyle style,
			PlotType type) throws IOException, InterruptedException {
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");

		Log.infoSep();
		Log.info("Sequentially plotting Distributions and / or NodeValueLists");

		ArrayList<Plot> distributionPlots = new ArrayList<Plot>();
		ArrayList<Plot> nodeValueListPlots = new ArrayList<Plot>();
		if (plotDistributions) {
			distributionPlots = Plotting.generateDistributionPlots(initBatch,
					batches, timestamps, dstDir, title, style, type);
		}
		if (plotNodeValues) {
			nodeValueListPlots = Plotting.generateNodeValueListPlots(initBatch,
					batches, timestamps, dstDir, title, style, type);
		}

		for (int i = 0; i < batches.length; i++) {
			AggregatedBatch tempBatch;
			long timestamp = Dir.getTimestamp(batches[i]);

			if (singleFile)
				tempBatch = AggregatedBatch.readFromSingleFile(aggrDir,
						timestamp, Dir.delimiter,
						BatchReadMode.readOnlyDistAndNvl);
			else
				tempBatch = AggregatedBatch.read(
						Dir.getBatchDataDir(aggrDir, timestamp), timestamp,
						BatchReadMode.readOnlyDistAndNvl);

			// append data to distribution plots
			for (Plot p : distributionPlots) {
				p.addDataSequentially(tempBatch);
			}
			// append data to nvl plots
			for (Plot p : nodeValueListPlots) {
				p.addDataSequentially(tempBatch);

			}

			// free resources
			tempBatch = null;
			System.gc();
		}

		// close and execute plot scripts
		for (Plot p : distributionPlots) {
			p.close();
			p.execute();
		}
		for (Plot p : nodeValueListPlots) {
			p.close();
			p.execute();
		}

	}

	/** Generates NodeValueList Plots **/
	private static ArrayList<Plot> generateNodeValueListPlots(
			AggregatedBatch initBatch, String[] batches, double[] timestamps,
			String dstDir, String title, PlotStyle style, PlotType type)
			throws IOException {
		ArrayList<Plot> nodevaluesPlots = new ArrayList<Plot>();

		// gather all available nvls
		for (AggregatedMetric m : initBatch.getMetrics().getList()) {
			String metric = m.getName();
			for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
				String nodevaluelist = n.getName();
				Log.info("\tplotting '" + nodevaluelist + "'");

				// generate normal plots
				PlotData[] nPlotData = new PlotData[batches.length];
				for (int i = 0; i < batches.length; i++) {
					PlotData plotData = PlotData.get(nodevaluelist, metric,
							style, title + " @ " + timestamps[i], type);
					nPlotData[i] = plotData;
				}

				Plot nPlot = new Plot(dstDir,
						PlotFilenames.getNodeValueListPlot(metric,
								nodevaluelist + 2),
						PlotFilenames.getNodeValueListGnuplotScript(metric,
								nodevaluelist + 2), nodevaluelist + " (" + type
								+ ")", nPlotData);
				nPlot.setNodeValueListOrder(NodeValueListOrder.ascending);
				nodevaluesPlots.add(nPlot);
			}
		}

		// write headers
		for (Plot p : nodevaluesPlots) {
			p.writeScriptHeaderNeu();
		}
		return nodevaluesPlots;
	}

	/** Generates Distribution Plots **/
	private static ArrayList<Plot> generateDistributionPlots(
			AggregatedBatch initBatch, String[] batches, double[] timestamps,
			String dstDir, String title, PlotStyle style, PlotType type)
			throws IOException {
		ArrayList<Plot> distributionPlots = new ArrayList<Plot>();

		// gather all available distributions
		for (AggregatedMetric m : initBatch.getMetrics().getList()) {
			Log.info("plotting metric " + m.getName());
			String metric = m.getName();
			for (AggregatedDistribution d : m.getDistributions().getList()) {
				String distribution = d.getName();
				Log.info("\tplotting '" + distribution + "'");

				// generate normal plots
				PlotData[] dPlotData = new PlotData[batches.length];
				for (int i = 0; i < batches.length; i++) {
					dPlotData[i] = PlotData.get(distribution, metric, style,
							title + " @ " + timestamps[i], type);
				}

				distributionPlots.add(new Plot(dstDir, PlotFilenames
						.getDistributionPlot(metric, distribution),
						PlotFilenames.getDistributionGnuplotScript(metric,
								distribution),
						distribution + " (" + type + ")", dPlotData));

				// generate cdf plots
				PlotData[] dPlotDataCDF = new PlotData[batches.length];
				for (int i = 0; i < batches.length; i++) {
					PlotData cdfPlotData = PlotData.get(distribution, metric,
							style, title + " @ " + timestamps[i], type);
					cdfPlotData.setPlotAsCdf(true);
					dPlotDataCDF[i] = cdfPlotData;
				}
				distributionPlots.add(new Plot(dstDir, PlotFilenames
						.getDistributionCdfPlot(metric, distribution),
						PlotFilenames.getDistributionCdfGnuplotScript(metric,
								distribution), "CDF of " + distribution + " ("
								+ type + ")", dPlotDataCDF));
			}
		}

		// write headers
		for (Plot p : distributionPlots) {
			p.writeScriptHeaderNeu();
		}
		return distributionPlots;
	}

	/** Plot statistics **/
	private static void plotStatistics(AggregatedBatch[] batchData,
			double[] timestamps, AggregatedValueList values, String dstDir,
			String title, PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("Plotting values:");
		for (String value : SeriesStats.statisticsToPlot) {
			if (values.getNames().contains(value)) {
				Log.info("\tplotting '" + value + "'");

				// get plot data
				PlotData valuePlotData = PlotData.get(value,
						Config.get("PLOT_STATISTICS"), style, title, type);

				// create plot
				Plot valuePlot = new Plot(dstDir, PlotFilenames.getValuesPlot(
						Config.get("PREFIX_STATS_PLOT"), value),
						PlotFilenames.getValuesGnuplotScript(
								Config.get("PREFIX_STATS_PLOT"), value), value
								+ " (" + type + ")",
						new PlotData[] { valuePlotData });

				// write header
				valuePlot.writeScriptHeaderNeu();

				// append data
				valuePlot.addData(batchData);

				// close and execute
				valuePlot.close();
				valuePlot.execute();
			}
		}
		// TODO: combination of values ?
	}

	/** Plots metric values **/
	private static void plotMetricValues(AggregatedBatch[] batchData,
			double[] timestamps, AggregatedMetricList metrics, String dstDir,
			String title, PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();

		for (AggregatedMetric m : metrics.getList()) {
			String metric = m.getName();
			Log.info("Plotting metric " + metric);
			for (AggregatedValue v : m.getValues().getList()) {
				String value = v.getName();
				Log.info("\tplotting '" + value + "'");

				// get plot data
				PlotData valuePlotData = PlotData.get(value, m.getName(),
						style, title, type);

				// create plot
				Plot valuePlot = new Plot(dstDir, PlotFilenames.getValuesPlot(
						metric, value), PlotFilenames.getValuesGnuplotScript(
						metric, value), value + " (" + type + ")",
						new PlotData[] { valuePlotData });

				// write header
				valuePlot.writeScriptHeaderNeu();

				// append data
				valuePlot.addData(batchData);

				// close and execute
				valuePlot.close();
				valuePlot.execute();
			}
		}

		// TODO: CONFIGURABLE? HEURISTICS VS EXACTS?
	}

	/** Plots metric runtimes **/
	private static void plotMetricRuntimes(AggregatedBatch[] batchData,
			double[] timestamps, AggregatedRunTimeList metricRuntimes,
			String dstDir, String title, PlotStyle style, PlotType type)
			throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("Plotting Metric-Runtimes:");

		PlotData[] metRuntimes = new PlotData[metricRuntimes.size()];
		int index = 0;

		// plot single runtime plots
		for (AggregatedValue met : metricRuntimes.getList()) {
			String runtime = met.getName();
			Log.info("\tplotting '" + runtime + "'");

			// get plot data
			PlotData metPlotData = PlotData.get(runtime,
					Config.get("PLOT_METRICRUNTIMES"), style, title, type);
			metRuntimes[index] = metPlotData;

			// create plot
			Plot metRuntimeSinglePlot = new Plot(dstDir,
					PlotFilenames.getRuntimesMetricPlot(runtime),
					PlotFilenames.getRuntimesGnuplotScript(runtime), runtime
							+ " (" + type + ")", new PlotData[] { metPlotData });
			Plot metRuntimeSinglePlotCDF = new Plot(dstDir,
					PlotFilenames.getRuntimesMetricPlotCDF(runtime),
					PlotFilenames.getRuntimesGnuplotScriptCDF(runtime),
					"CDF of " + runtime + " (" + type + ")",
					new PlotData[] { metPlotData });

			// write header
			metRuntimeSinglePlot.writeScriptHeaderNeu();
			metRuntimeSinglePlotCDF.writeScriptHeaderNeu();

			// append data
			metRuntimeSinglePlot.addData(batchData);
			metRuntimeSinglePlotCDF.addDataFromRuntimesAsCDF(batchData);

			// close and execute
			metRuntimeSinglePlot.close();
			metRuntimeSinglePlotCDF.close();

			metRuntimeSinglePlot.execute();
			metRuntimeSinglePlotCDF.execute();

			index++;
		}

		// create combined plots
		String metricRuntimeName = Config.get("PLOT_METRIC_RUNTIMES");
		Plot metricRuntimesPlot = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlot(metricRuntimeName),
				PlotFilenames.getRuntimesGnuplotScript(metricRuntimeName),
				metricRuntimeName + " runtimes (" + type + ")", metRuntimes);
		Plot metricRuntimesPlotCDF = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlotCDF(metricRuntimeName),
				PlotFilenames.getRuntimesGnuplotScriptCDF(metricRuntimeName),
				"CDF of " + metricRuntimeName + " runtimes (" + type + ")",
				metRuntimes);

		// write headers
		metricRuntimesPlot.writeScriptHeaderNeu();
		metricRuntimesPlotCDF.writeScriptHeaderNeu();

		// add data to metric runtime plot
		metricRuntimesPlot.addData(batchData);

		// add cdf data to metric runtime cdf plot
		metricRuntimesPlotCDF.addDataFromRuntimesAsCDF(batchData);

		// close and execute
		metricRuntimesPlot.close();
		metricRuntimesPlot.execute();

		metricRuntimesPlotCDF.close();
		metricRuntimesPlotCDF.execute();
	}

	/** Plot general runtimes **/
	private static void plotGeneralRuntimes(AggregatedBatch[] batchData,
			double[] timestamps, ArrayList<String> y, String dstDir,
			String title, PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("Plotting General-Runtimes:");
		PlotData[] genRuntimes = new PlotData[y.size()];
		int index = 0;
		for (String gen : y) {
			Log.info("\tplotting '" + gen + "'");
			genRuntimes[index] = PlotData.get(gen,
					Config.get("PLOT_GENERALRUNTIMES"), style, title, type);
			index++;
		}

		// create plots
		String generalRuntimeName = Config.get("PLOT_GENERAL_RUNTIMES");
		Plot generalRuntimesPlot = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlot(generalRuntimeName),
				PlotFilenames.getRuntimesGnuplotScript(generalRuntimeName),
				generalRuntimeName + " runtimes (" + type + ")", genRuntimes);
		Plot generalRuntimesPlotCDF = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlotCDF(generalRuntimeName),
				PlotFilenames.getRuntimesGnuplotScriptCDF(generalRuntimeName),
				"CDF of " + generalRuntimeName + " runtimes (" + type + ")",
				genRuntimes);

		// write headers
		generalRuntimesPlot.writeScriptHeaderNeu();
		generalRuntimesPlotCDF.writeScriptHeaderNeu();

		// add data to general runtime plot
		generalRuntimesPlot.addData(batchData);

		// add cdf data to general runtime cdf plot
		generalRuntimesPlotCDF.addDataFromRuntimesAsCDF(batchData);

		// close and execute
		generalRuntimesPlot.close();
		generalRuntimesPlot.execute();

		generalRuntimesPlotCDF.close();
		generalRuntimesPlotCDF.execute();
	}

	/**
	 * Plots data from the given SeriesData's. Which batches are plotted is
	 * defined by the given parameters.
	 * 
	 * Example:
	 * 
	 * plot(data, dir, 2, 12, 3) - will plot every third batch between 2 and 12
	 * -> batches 2, 5, 8, 11
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory where plots and scripts will be written
	 * @param timestampFrom
	 *            Starting timestamp
	 * @param timestampTo
	 *            Ending timestamp
	 * @param stepSize
	 *            StepSize between batches.
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotFromTo(SeriesData[] seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepSize)
			throws IOException, InterruptedException {
		Plotting.plotFromTo(
				seriesData,
				dstDir,
				timestampFrom,
				timestampTo,
				stepSize,
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE"),
				Config.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"),
				Config.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"));
	}

	/**
	 * Main plotting method that handles the whole plotting process.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory where plots and scripts will be written
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @param sortBy
	 *            Argument the NodeValueList will be sorted by
	 * @param sortOrder
	 *            Sorting order
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plot(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, DistributionPlotType distPlotType,
			NodeValueListOrderBy sortBy, NodeValueListOrder sortOrder)
			throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("plotting all data for " + seriesData.length + " series ("
				+ type + "/" + style + ")");
		(new File(dstDir)).mkdirs();

		// read aggregation data
		for (int i = 0; i < seriesData.length; i++) {
			seriesData[i].setAggregation(AggregatedSeries.read(
					seriesData[i].getDir(), seriesData[i].getName() + i + "_"
							+ Config.get("RUN_AGGREGATION"),
					BatchReadMode.readAllValues));
		}

		// plot different data from the aggregation data
		Plotting.plotDistributions(seriesData, dstDir, type, style,
				distPlotType);
		Plotting.plotValues(seriesData, dstDir, type, style);
		Plotting.plotStatistics(seriesData, dstDir, type, style);
		Plotting.plotRuntimes(seriesData, dstDir, type, style);
		Plotting.plotNodeValueLists(seriesData, dstDir, type, style, sortBy,
				sortOrder);
	}

	/**
	 * Plots Distributions by calling Plotting.plotDistribution for each
	 * distribution.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotDistributions(SeriesData[] seriesData,
			String dstDir, PlotType type, PlotStyle style,
			DistributionPlotType distPlotType) throws IOException,
			InterruptedException {
		Log.info("plotting distributions with " + distPlotType.toString());

		for (AggregatedMetric m : seriesData[0].getAggregation().getBatches()[0]
				.getMetrics().getList()) {
			for (AggregatedDistribution d : m.getDistributions().getList()) {
				Plotting.plotDistributon(seriesData, dstDir, type, style,
						distPlotType, m.getName(), d.getName());
			}
		}
	}

	/**
	 * Plots distributions with default PlotTypes and PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotDistributions(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotDistributions(seriesData, dstDir,
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"),
				Config.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE"));
	}

	/**
	 * Plots distributions of single series with default PlotTypes and
	 * PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotDistributions(SeriesData series, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotDistributions(new SeriesData[] { series }, dstDir);
	}

	/**
	 * Plots NodeValueLists by calling Plotting.plotNodeValueList for each list.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @param sortBy
	 *            Argument the NodeValueList will be sorted by
	 * @param sortOrder
	 *            Sorting order
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotNodeValueLists(SeriesData[] seriesData,
			String dstDir, PlotType type, PlotStyle style,
			NodeValueListOrderBy sortBy, NodeValueListOrder sortOrder)
			throws IOException, InterruptedException {
		Log.info("plotting nodevaluelists sorted by " + sortBy.toString()
				+ " in " + sortOrder.toString() + " order");
		for (AggregatedMetric m : seriesData[0].getAggregation().getBatches()[0]
				.getMetrics().getList()) {
			for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
				Plotting.plotNodeValueList(seriesData, dstDir, type, style,
						m.getName(), n.getName(), sortBy, sortOrder);
			}
		}
	}

	/**
	 * Plots NodeValueLists with default PlotType, PlotStyles and
	 * NVL-Orderoptions.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotNodeValueLists(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotNodeValueLists(seriesData, dstDir,
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"),
				Config.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"),
				Config.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"));
	}

	/**
	 * Plots NodeValueLists of single series with default PlotType, PlotStyles
	 * and NVL-Orderoptions.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotNodeValueLists(SeriesData series, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotNodeValueLists(new SeriesData[] { series }, dstDir);
	}

	/**
	 * Plots Metric-Values by calling Plotting.plotValue for each value.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotValues(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting values");
		for (AggregatedMetric metric : seriesData[0].getAggregation()
				.getBatches()[0].getMetrics().getList()) {
			for (AggregatedValue value : metric.getValues().getList()) {
				Plotting.plotValue(seriesData, dstDir, type, style,
						metric.getName(), value.getName());
			}
		}
	}

	/**
	 * Plots Metric-Values with default PlotType and PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotValues(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotValues(seriesData, dstDir,
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"));
	}

	/**
	 * Plots Metric-Values of a single series with default PlotType and
	 * PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotValues(SeriesData series, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotValues(new SeriesData[] { series }, dstDir);
	}

	/**
	 * Plots statistics by calling Plotting.plotValue for each statistical
	 * value.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotStatistics(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting statistics");
		for (String value : SeriesStats.statisticsToPlot) {
			Plotting.plotValue(seriesData, dstDir, type, style, null, value);
		}
	}

	/**
	 * Plots statistics with default PlotTypes and PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotStatistics(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotStatistics(seriesData, dstDir,
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"));
	}

	/**
	 * Plots statistics of a single series with default PlotTypes and
	 * PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotStatistics(SeriesData series, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotStatistics(new SeriesData[] { series }, dstDir);
	}

	/**
	 * Plots runtimes for each batch of each series.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotRuntimes(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting runtimes");
		for (SeriesData s : seriesData) {
			if (s.getAggregation().getBatches().length < 2) {
				return;
			}
		}

		int gr = SeriesStats.generalRuntimesOfCombinedPlot.length
				* seriesData.length;
		int mr = 0;
		for (SeriesData s : seriesData) {
			mr += s.getAggregation().getBatches()[0].getMetricRuntimes().size();
		}

		for (String runtime : SeriesStats.generalRuntimesToPlot) {
			AggregatedValue[][] runtimes = new AggregatedValue[seriesData.length][];
			String[] names = new String[seriesData.length];
			long[][] x = new long[seriesData.length][];
			int index = 0;

			for (SeriesData s : seriesData) {
				runtimes[index] = getGeneralRuntimes(s.getAggregation(),
						runtime);
				x[index] = getX(s.getAggregation());
				names[index] = s.getName();
				index++;
			}
			Plotting.plot(runtimes, x, names, dstDir, PlotFilenames
					.getRuntimesStatisticPlot(runtime), PlotFilenames
					.getRuntimesGnuplotScript(PlotFilenames
							.getRuntimesStatisticPlot(runtime)), runtime + " ("
					+ type + ")", type, style);

			// plot cdf test
			AggregatedValue[][] runtimesCDF = new AggregatedValue[seriesData.length][];
			for (int i = 0; i < runtimes.length; i++) {
				AggregatedValue value = runtimes[i][0];
				AggregatedValue[] aggrValues = new AggregatedValue[runtimes[i].length];
				for (int j = 0; j < runtimes[i].length; j++) {
					double[] v1 = value.getValues();
					if (j != 0) {
						double[] v2 = runtimes[i][j].getValues();
						for (int k = 0; k < v1.length; k++) {
							v1[k] += v2[k];
						}
					}
					double[] v3 = new double[v1.length];
					System.arraycopy(v1, 0, v3, 0, v1.length);
					aggrValues[j] = new AggregatedValue(
							runtimes[i][j].getName(), v3);
				}
				runtimesCDF[i] = aggrValues;
			}
			Plotting.plot(runtimesCDF, x, names, dstDir, PlotFilenames
					.getRuntimesStatisticPlotCDF(runtime), PlotFilenames
					.getRuntimesGnuplotScript(PlotFilenames
							.getRuntimesStatisticPlotCDF(runtime)), "CDF of "
					+ runtime + " (" + type + ")", type, style);
		}

		for (String metric : seriesData[0].getAggregation().getBatches()[0]
				.getMetricRuntimes().getNames()) {
			AggregatedValue[][] runtimes = new AggregatedValue[seriesData.length][];
			String[] names = new String[seriesData.length];
			long[][] x = new long[seriesData.length][];
			int index = 0;
			for (SeriesData s : seriesData) {
				runtimes[index] = getMetricRuntimes(s.getAggregation(), metric);
				x[index] = getX(s.getAggregation());
				names[index] = s.getName();
				index++;
			}
			Plotting.plot(runtimes, x, names, dstDir, PlotFilenames
					.getRuntimesMetricPlot(metric), PlotFilenames
					.getRuntimesGnuplotScript(PlotFilenames
							.getRuntimesMetricPlot(metric)), metric + " ("
					+ type + ")", type, style);

			// plot cdf test
			AggregatedValue[][] runtimesCDF = new AggregatedValue[seriesData.length][];
			for (int i = 0; i < runtimes.length; i++) {
				AggregatedValue value = runtimes[i][0];
				AggregatedValue[] aggrValues = new AggregatedValue[runtimes[i].length];
				for (int j = 0; j < runtimes[i].length; j++) {
					double[] v1 = value.getValues();
					if (j != 0) {
						double[] v2 = runtimes[i][j].getValues();
						for (int k = 0; k < v1.length; k++) {
							v1[k] += v2[k];
						}
					}
					double[] v3 = new double[v1.length];
					System.arraycopy(v1, 0, v3, 0, v1.length);
					aggrValues[j] = new AggregatedValue(
							runtimes[i][j].getName(), v3);
				}
				runtimesCDF[i] = aggrValues;
			}
			Plotting.plot(runtimesCDF, x, names, dstDir, PlotFilenames
					.getRuntimesMetricPlotCDF(metric), PlotFilenames
					.getRuntimesGnuplotScript(PlotFilenames
							.getRuntimesMetricPlotCDF(metric)), "CDF of "
					+ metric + " (" + type + ")", type, style);
		}

		// gather runtime data..
		AggregatedValue[][] general = new AggregatedValue[gr][];
		AggregatedValue[][] metrics = new AggregatedValue[mr][];
		String[] generalNames = new String[gr];
		String[] metricsNames = new String[mr];
		long[][] generalX = new long[gr][];
		long[][] metricsX = new long[mr][];

		AggregatedValue[][] generalCDF = new AggregatedValue[gr][];
		AggregatedValue[][] metricsCDF = new AggregatedValue[mr][];

		int index1 = 0;
		int index2 = 0;

		// for each series
		for (SeriesData s : seriesData) {
			// for each statistic
			for (String runtime : SeriesStats.generalRuntimesOfCombinedPlot) {
				general[index1] = getGeneralRuntimes(s.getAggregation(),
						runtime);
				generalX[index1] = getX(s.getAggregation());
				generalNames[index1] = runtime + "-" + s.getName();
				index1++;
			}
			// for each metric
			for (String metric : s.getAggregation().getBatches()[0]
					.getMetricRuntimes().getNames()) {
				metrics[index2] = getMetricRuntimes(s.getAggregation(), metric);
				metricsX[index2] = getX(s.getAggregation());
				metricsNames[index2] = metric + "-" + s.getName();
				index2++;
			}
		}

		// generate plot script for runtime statistics and execute it
		Plotting.plot(general, generalX, generalNames, dstDir, PlotFilenames
				.getRuntimesStatisticPlot(Config.get("PLOT_GENERAL_RUNTIMES")),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesStatisticPlot(Config
								.get("PLOT_GENERAL_RUNTIMES"))),
				"general runtimes (" + type + ")", type, style);

		for (int i = 0; i < general.length; i++) {
			// generate cdf plot
			AggregatedValue value = general[i][0];
			AggregatedValue[] aggrValues = new AggregatedValue[general[i].length];
			for (int j = 0; j < general[i].length; j++) {
				double[] v1 = value.getValues();
				if (j != 0) {
					double[] v2 = general[i][j].getValues();
					for (int k = 0; k < v1.length; k++) {
						v1[k] += v2[k];
					}
				}
				double[] v3 = new double[v1.length];
				System.arraycopy(v1, 0, v3, 0, v3.length);
				aggrValues[j] = new AggregatedValue(general[i][j].getName(), v3);
			}
			generalCDF[i] = aggrValues;
		}

		// generate CDF plot script for runtime statistics and execute it
		Plotting.plot(generalCDF, generalX, generalNames, dstDir, PlotFilenames
				.getRuntimesStatisticPlotCDF(Config
						.get("PLOT_GENERAL_RUNTIMES")), PlotFilenames
				.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesStatisticPlotCDF(Config
								.get("PLOT_GENERAL_RUNTIMES"))),
				"CDF of general runtimes (" + type + ")", type, style);

		// generate plot script for metric runtimes and execute it
		Plotting.plot(metrics, metricsX, metricsNames, dstDir, PlotFilenames
				.getRuntimesMetricPlot(Config.get("PLOT_METRIC_RUNTIMES")),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesMetricPlot(Config
								.get("PLOT_METRIC_RUNTIMES"))),
				"metric runtimes (" + type + ")", type, style);

		for (int i = 0; i < metrics.length; i++) {
			// generate cdf plots
			AggregatedValue value = metrics[i][0];
			AggregatedValue[] aggrValues = new AggregatedValue[metrics[i].length];
			for (int j = 0; j < metrics[i].length; j++) {
				double[] v1 = value.getValues();
				if (j != 0) {
					double[] v2 = metrics[i][j].getValues();
					for (int k = 0; k < v1.length; k++) {
						v1[k] += v2[k];
					}
				}
				double[] v3 = new double[v1.length];
				System.arraycopy(v1, 0, v3, 0, v1.length);
				aggrValues[j] = new AggregatedValue(metrics[i][j].getName(), v3);
			}
			metricsCDF[i] = aggrValues;
		}

		// generate CDF plot script for metric runtimes and execute it
		Plotting.plot(metricsCDF, metricsX, metricsNames, dstDir, PlotFilenames
				.getRuntimesMetricPlotCDF(Config.get("PLOT_METRIC_RUNTIMES")),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesMetricPlotCDF(Config
								.get("PLOT_METRIC_RUNTIMES"))),
				"CDF of metric runtimes (" + type + ")", type, style);
	}

	/**
	 * Plots runtimes for each batch of each series with default PlotTypes and
	 * PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotRuntimes(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotRuntimes(seriesData, dstDir,
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"));
	}

	/**
	 * Plots runtimes of a single series with default PlotTypes and PlotStyles.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            destination directory
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	public static void plotRuntimes(SeriesData series, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plotRuntimes(new SeriesData[] { series }, dstDir);
	}

	/**
	 * Plots one Distribution of the input seriesData.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @param metric
	 *            the metric which is being plotted
	 * @param distribution
	 *            the distribution which is being plotted
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown in Execute.exec
	 */
	private static void plotDistributon(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, DistributionPlotType distPlotType,
			String metric, String distribution) throws IOException,
			InterruptedException {
		Log.info("distribution " + distribution + " of " + metric + " with "
				+ distPlotType.toString());
		int batches = 0;
		for (SeriesData s : seriesData) {
			batches += s.getAggregation().getBatches().length;
		}

		PlotData[] data = new PlotData[batches];
		int index = 0;

		// gather data..
		AggregatedDistribution[] DistributionTemp = new AggregatedDistribution[batches];
		// for each series
		for (SeriesData s : seriesData) {
			// for each batch
			for (AggregatedBatch b : s.getAggregation().getBatches()) {
				DistributionTemp[index] = b.getMetrics().get(metric)
						.getDistributions().get(distribution);
				String path = Dir.getAggregatedMetricDataDir(s.getDir(),
						b.getTimestamp(), metric)
						+ distribution + Config.get("SUFFIX_DIST");
				data[index++] = PlotData.get(path, metric, style, s.getName()
						+ " @ " + b.getTimestamp(), type);
			}
		}
		// generate plot script and execute it
		switch (distPlotType) {
		case distOnly:
			Plot plotDistOnly = new Plot(data, dstDir,
					PlotFilenames.getDistributionPlot(metric, distribution),
					PlotFilenames.getDistributionGnuplotScript(metric,
							distribution), distPlotType);
			plotDistOnly.setTitle(distribution + " (" + type + ")");
			plotDistOnly.generate(DistributionTemp);
			break;
		case cdfOnly:
			Plot plotCdfOnly = new Plot(data, dstDir,
					PlotFilenames.getDistributionCdfPlot(metric, distribution),
					PlotFilenames.getDistributionCdfGnuplotScript(metric,
							distribution), distPlotType);
			plotCdfOnly.setTitle("CDF of " + distribution + " (" + type + ")");
			plotCdfOnly.generate(DistributionTemp);
			break;
		case distANDcdf:
			Plot plotDist = new Plot(data, dstDir,
					PlotFilenames.getDistributionPlot(metric, distribution),
					PlotFilenames.getDistributionGnuplotScript(metric,
							distribution), DistributionPlotType.distOnly);
			plotDist.setTitle(distribution + " (" + type + ")");
			plotDist.generate(DistributionTemp);
			Plot plotCdf = new Plot(data, dstDir,
					PlotFilenames.getDistributionCdfPlot(metric, distribution),
					PlotFilenames.getDistributionCdfGnuplotScript(metric,
							distribution), DistributionPlotType.cdfOnly);
			plotCdf.setTitle("CDF of " + distribution + " (" + type + ")");
			plotCdf.generate(DistributionTemp);
			break;
		}

	}

	/**
	 * Plots one NodeValueList of the input seriesData.
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @param metric
	 *            the metric which is being plotted
	 * @param nodevaluelist
	 *            the nodevaluelist which is being plotted
	 * @param sortBy
	 *            Argument the NodeValueList will be sorted by
	 * @param sortOrder
	 *            Sorting order
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown when metric is null or in Execute.exec
	 */
	private static void plotNodeValueList(SeriesData[] seriesData,
			String dstDir, PlotType type, PlotStyle style, String metric,
			String nodevaluelist, NodeValueListOrderBy sortBy,
			NodeValueListOrder sortOrder) throws IOException,
			InterruptedException {
		Log.info("nodevaluelist " + nodevaluelist + " of " + metric);
		if (metric == null)
			throw new InterruptedException("null pointer on metric");
		int batches = 0;
		for (SeriesData s : seriesData) {
			batches += s.getAggregation().getBatches().length;
		}

		PlotData[] data = new PlotData[batches];
		int index = 0;

		// gather data..
		AggregatedNodeValueList[] NodeValueListsTemp = new AggregatedNodeValueList[batches];
		// for each series
		for (SeriesData s : seriesData) {
			// for each batch
			for (AggregatedBatch b : s.getAggregation().getBatches()) {
				NodeValueListsTemp[index] = b.getMetrics().get(metric)
						.getNodeValues().get(nodevaluelist);
				NodeValueListsTemp[index].setsortIndex(sortBy, sortOrder);
				String path = Dir.getAggregatedMetricDataDir(s.getDir(),
						b.getTimestamp(), metric)
						+ nodevaluelist + Config.get("SUFFIX_NVL");
				path = dstDir
						+ PlotFilenames.getNodeValueListDataFile(metric,
								nodevaluelist, (int) b.getTimestamp());
				data[index++] = PlotData.get(path, metric, style, s.getName()
						+ " @ " + b.getTimestamp(), type);
			}
		}
		// generate plot script and execute it
		Plot plot = new Plot(data, dstDir, PlotFilenames.getNodeValueListPlot(
				metric, nodevaluelist),
				PlotFilenames.getNodeValueListGnuplotScript(metric,
						nodevaluelist));
		plot.setTitle(nodevaluelist + " (" + type + ")");
		plot.generate(NodeValueListsTemp);
	}

	/**
	 * Plots a value for each series
	 * 
	 * @param seriesData
	 *            SeriesData which will be plotted
	 * @param dstDir
	 *            Destination directory
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @param metric
	 *            the metric which is being plotted
	 * @param value
	 *            the value which is being plotted
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown when metric is null or in Execute.exec
	 */
	private static void plotValue(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, String metric, String value)
			throws IOException, InterruptedException {

		int index1 = 0;
		String m = metric == null ? Config.get("PREFIX_STATS_PLOT") : metric;

		PlotData[] allData = new PlotData[seriesData.length];
		AggregatedValue[][] allValues = new AggregatedValue[seriesData.length][0];
		long[][] timestamps = new long[seriesData.length][];
		for (int i = 0; i < seriesData.length; i++) {
			allValues[i] = new AggregatedValue[seriesData[i].getAggregation()
					.getBatches().length];
		}
		// gather data..
		// for each series
		for (SeriesData s : seriesData) {
			int index2 = 0;
			long[] timestampsTemp = new long[s.getAggregation().getBatches().length];
			// for each batch
			for (AggregatedBatch b : s.getAggregation().getBatches()) {
				if (metric == null) {
					allValues[index1][index2] = b.getValues().get(value);
				} else {
					allValues[index1][index2] = b.getMetrics().get(metric)
							.getValues().get(value);
				}
				timestampsTemp[index2] = b.getTimestamp();
				index2++;
			}
			String path = PlotFilenames.getValuesGnuplotScript(m, value + "."
					+ "ALL");

			allData[index1] = PlotData.get(path, metric, style, s.getName(),
					type);
			timestamps[index1] = timestampsTemp;
			index1++;
		}

		// generate plot script and execute it
		Plot plot = new Plot(allData, dstDir, PlotFilenames.getValuesPlot(m,
				value + "." + "ALL"), PlotFilenames.getValuesGnuplotScript(m,
				value + "." + "ALL"));
		plot.setTitle(value + " (" + type + ")");
		plot.generate(allValues, timestamps);
	}

	private static Writer plotValue2(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, String metric, String value)
			throws IOException {
		// if no metric set its a stats plot, set prefix
		String m = metric == null ? Config.get("PREFIX_STATS_PLOT") : metric;

		String title = value + " (" + type + ")";
		PlotData[] allData = new PlotData[seriesData.length];
		for (int i = 0; i < allData.length; i++) {
			allData[i] = PlotData.get(
					PlotFilenames.getValuesGnuplotScript(m,
							value + Config.get("FILE_NAME_DELIMITER") + "ALL"),
					metric, style, seriesData[i].getName(), type);
		}

		Plot plot = new Plot(allData, dstDir, PlotFilenames.getValuesPlot(m,
				value + Config.get("FILE_NAME_DELIMITER") + "ALL"),
				PlotFilenames.getValuesGnuplotScript(m,
						value + Config.get("FILE_NAME_DELIMITER") + "ALL"));
		plot.setTitle(title);

		return plot.writeScriptHeader(
				dstDir,
				PlotFilenames.getValuesGnuplotScript(m,
						value + Config.get("FILE_NAME_DELIMITER") + "ALL"));
	}

	/**
	 * This method is called in Plotting.plotRuntimes to plot the runtime
	 * values.
	 * 
	 * @param values
	 *            2-dimensional AggregatedValue-array containing the values to
	 *            be plotted.
	 * @param names
	 *            names of the plotted values
	 * @param dstDir
	 *            destination directory
	 * @param filename
	 *            destination filename
	 * @param script
	 *            script filename
	 * @param title
	 *            plot title
	 * @param type
	 *            PlotType
	 * @param style
	 *            PlotStyle
	 * @throws IOException
	 *             thrown by the writer in Plot.writeScript or in Execute.exec
	 * @throws InterruptedException
	 *             thrown when metric is null or in Execute.exec
	 */
	private static void plot(AggregatedValue[][] values, long[][] timestamps,
			String[] names, String dstDir, String filename, String script,
			String title, PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		PlotData[] data = new PlotData[values.length];
		// gather data
		for (int i = 0; i < values.length; i++) {
			data[i] = PlotData.get(
					dstDir + PlotFilenames.getRuntimesDataFile(names[i]),
					Config.get("PLOT_STATISTICS"), style, names[i], type);
		}
		// generate plot script and execute it
		Plot plot = new Plot(data, dstDir, filename, script);
		plot.setTitle(title);
		plot.generate(values, timestamps);
	}

	/**
	 * This method returns the general runtimes of all aggregated batches. Note:
	 * The initialization batch will not be included. It's assumed that batch
	 * with lowest timestamp is initiliazation batch.
	 * 
	 * @param aggregation
	 * @param runtime
	 * @return
	 */
	private static AggregatedValue[] getGeneralRuntimes(
			AggregatedSeries aggregation, String runtime) {
		AggregatedValue[] values = new AggregatedValue[aggregation.getBatches().length - 1];

		// min is used to figure which batch is the initialization batch
		long min = 0;
		boolean init = false;
		for (AggregatedBatch aggBatch : aggregation.getBatches()) {
			if (!init) {
				min = aggBatch.getTimestamp();
				init = true;
			}
			if (aggBatch.getTimestamp() < min) {
				min = aggBatch.getTimestamp();
			}
		}

		// gather aggregated values
		int offset = 0;
		for (int i = 0; i < aggregation.getBatches().length; i++) {
			if (aggregation.getBatches()[i].getTimestamp() != min) {
				values[i - offset] = aggregation.getBatches()[i]
						.getGeneralRuntimes().get(runtime)
						.clone(1.0 / 1000.0 / 1000.0 / 1000.0);
				if (values[i - offset] == null) {
					values[i - offset] = AggregatedValue.getNaN();
				}
			} else {
				offset++;
			}
		}
		return values;
	}

	private static AggregatedValue[] getMetricRuntimes(
			AggregatedSeries aggregation, String metric) {
		AggregatedValue[] values = new AggregatedValue[aggregation.getBatches().length - 1];
		for (int i = 1; i < aggregation.getBatches().length; i++) {
			values[i - 1] = aggregation.getBatches()[i].getMetricRuntimes()
					.get(metric).clone(1.0 / 1000.0 / 1000.0 / 1000.0);
			if (values[i - 1] == null) {
				values[i - 1] = AggregatedValue.getNaN();
			}
		}
		return values;
	}

	private static long[] getX(AggregatedSeries aggregation) {
		long[] x = new long[aggregation.getBatches().length - 1];
		for (int i = 1; i < aggregation.getBatches().length; i++) {
			x[i - 1] = aggregation.getBatches()[i].getTimestamp();
		}
		return x;
	}

}
