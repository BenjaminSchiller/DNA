package dna.plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dna.io.filesystem.Dir;
import dna.io.filesystem.PlotFilenames;
import dna.plot.PlottingConfig.PlotFlag;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.series.SeriesStats;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedMetricList;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedValue;
import dna.series.aggdata.AggregatedValueList;
import dna.series.data.SeriesData;
import dna.util.Config;
import dna.util.Log;
import dna.util.Memory;

/**
 * Plotting class which holds static method for plotting.
 * 
 * @author Rwilmes
 * @date 19.05.2014
 */
public class Plotting {

	/**
	 * Main plotting method which handles the whole process of plotting. Takes a
	 * plotting config object which controls the behaviour. Which batches will
	 * be plotted is given by the parameters in the PlottingConfig.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	private static void plotFromTo(SeriesData[] seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		// read values from config
		long timestampFrom = config.getTimestampFrom();
		long timestampTo = config.getTimestampTo();
		long stepsize = config.getStepsize();
		PlotType type = config.getPlotType();
		PlotStyle style = config.getPlotStyle();
		NodeValueListOrder order = config.getNvlOrder();
		NodeValueListOrderBy orderBy = config.getNvlOrderBy();
		DistributionPlotType distPlotType = config.getDistPlotType();
		String title = seriesData[0].getName();
		ArrayList<String> combinedGeneralRuntimes = config.getGeneralRuntimes();
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");
		boolean plotDistributions = config.isPlotDistributions();
		boolean plotNodeValues = config.isPlotNodeValueLists();

		// log output
		Log.infoSep();
		Log.info("plotting data from " + seriesData.length + " series to "
				+ dstDir);

		// create dir
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

		// plot statistics
		if (config.isPlotStatistics()) {
			Plotting.plotStatistics(batchData, initBatch.getValues(), dstDir,
					title, style, type);

			// plot custom statistic plots
			if (config.getCustomStatisticPlots() != null) {
				if (config.getCustomStatisticPlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-Statistic-Plots:");
					Plotting.plotCustomValuePlots(batchData,
							config.getCustomStatisticPlots(), dstDir, title,
							style, type);
				}
			}
		}

		// plot custom value plots
		if (config.isPlotCustomValues()) {
			Log.infoSep();
			Log.info("Plotting Custom-Value-Plots:");
			Plotting.plotCustomValuePlots(batchData,
					config.getCustomValuePlots(), dstDir, title, style, type);
		}

		// plot runtimes
		if (config.isPlotRuntimes()) {
			// plot general runtimes
			Plotting.plotGeneralRuntimes(batchData, combinedGeneralRuntimes,
					dstDir, title, style, type);

			// plot metric runtimes
			Plotting.plotMetricRuntimes(batchData,
					initBatch.getMetricRuntimes(), dstDir, title, style, type);

			// plot custom runtimes
			Plotting.plotCustomRuntimes(batchData,
					config.getCustomRuntimePlots(), dstDir, title, style, type);
		}

		// plot metric values
		if (config.isPlotMetricValues()) {
			Plotting.plotMetricValues(batchData, initBatch.getMetrics(),
					dstDir, title, style, type);

			// plot custom metric value plots
			if (config.getCustomMetricValuePlots() != null) {
				if (config.getCustomMetricValuePlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-MetricValue-Plots:");
					Plotting.plotCustomValuePlots(batchData,
							config.getCustomMetricValuePlots(), dstDir, title,
							style, type);
				}
			}
		}

		// all at-once plots finished
		// print memory usage
		double mem1 = new Memory().getUsed();
		Log.infoSep();
		Log.info("");
		Log.info("Finished first plotting attempt");
		Log.info("\tused memory: " + mem1);
		Log.info("");
		Log.info("Erasing unsused data");

		// free resources
		batchData = null;
		System.gc();

		// print memory usage after resoruce freeing
		double mem2 = new Memory().getUsed();
		Log.info("\tremoved: " + (mem1 - mem2));
		Log.info("\tused memory (new): " + mem2);
		Log.info("");

		// plot distributions
		if (plotDistributions || plotNodeValues)
			Plotting.plotDistributionsAndNodeValues(plotDistributions,
					plotNodeValues, initBatch, batches, timestamps,
					config.getCustomDistributionPlots(),
					config.getCustomNodeValueListPlots(), tempDir, dstDir,
					title, style, type, distPlotType, order, orderBy);

	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData[] seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		Plotting.plotFromTo(new SeriesData[] { seriesData }, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param timestampFrom
	 *            Beginning of the timestamp interval to be plotted.
	 * @param timestampTo
	 *            Ending of the timestamp interval to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromTo(SeriesData[] seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepsize,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config = new PlottingConfig(flags);
		config.setPlotInterval(timestampFrom, timestampTo, stepsize);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param timestampFrom
	 *            Beginning of the timestamp interval to be plotted.
	 * @param timestampTo
	 *            Ending of the timestamp interval to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromTo(SeriesData seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepsize,
			PlotFlag... flags) throws IOException, InterruptedException {
		Plotting.plotFromTo(new SeriesData[] { seriesData }, dstDir,
				timestampFrom, timestampTo, stepsize, flags);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		// craft config
		PlottingConfig config = new PlottingConfig(PlotFlag.plotAll);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData[] seriesData, String dstDir,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config = new PlottingConfig(flags);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData seriesData, String dstDir,
			PlotFlag... flags) throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir, flags);
	}

	/**
	 * Plots only the statistics of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotStatistic(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotStatistics);
	}

	/**
	 * Plots only the statistics of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotStatistic(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotStatistics);
	}

	/**
	 * Plots only the metric values of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotMetricValues(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotMetricValues);
	}

	/**
	 * Plots only the metric values of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotMetricValues(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotMetricValues);
	}

	/**
	 * Plots only the custom values plots on the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotCustomValuePlots(SeriesData[] seriesData,
			String dstDir) throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotCustomValues);
	}

	/**
	 * Plots only the custom value plots on the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotCustomValuePlots(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotCustomValues);
	}

	/**
	 * Plots only the runtimes of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRuntimes(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotRuntimes);
	}

	/**
	 * Plots only the runtimes of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRuntimes(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotRuntimes);
	}

	/**
	 * Plots only the distributions of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotDistributions(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotDistributions);
	}

	/**
	 * Plots only the distributions of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotDistributions(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotDistributions);
	}

	/**
	 * Plots only the nodevaluelists of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotNodeValueLists(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotNodeValueLists);
	}

	/**
	 * Plots only the nodevaluelists of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotNodeValueLists(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotNodeValueLists);
	}

	/** Plots custom value plots **/
	private static void plotCustomValuePlots(AggregatedBatch[] batchData,
			ArrayList<PlotConfig> customValuePlots, String dstDir,
			String title, PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		for (PlotConfig pc : customValuePlots) {
			String name = pc.getName();
			if (name == null)
				continue;
			Log.info("\tplotting '" + name + "'");
			String[] values = pc.getValues();
			String[] domains = pc.getDomains();

			// gather plot data
			PlotData[] data = new PlotData[values.length];
			for (int j = 0; j < values.length; j++) {
				String value = values[j];
				String domain = domains[j];
				data[j] = PlotData.get(value, domain, style, domain + "."
						+ value + "-" + title, type);
			}

			// create plot
			Plot p = new Plot(dstDir, PlotFilenames.getValuesPlot(name),
					PlotFilenames.getValuesGnuplotScript(name), name + " ("
							+ type + ")", data);

			// write script header
			p.writeScriptHeader();

			// add data
			if (pc.isPlotAsCdf())
				p.addDataFromRuntimesAsCDF(batchData);
			else
				p.addData(batchData);

			// close and execute
			p.close();
			p.execute();
		}
	}

	/** Plots Distributions and NodeValueLists **/
	private static void plotDistributionsAndNodeValues(
			boolean plotDistributions, boolean plotNodeValues,
			AggregatedBatch initBatch, String[] batches, double[] timestamps,
			ArrayList<PlotConfig> customDistributionPlots,
			ArrayList<PlotConfig> customNodeValueListPlots, String aggrDir,
			String dstDir, String title, PlotStyle style, PlotType type,
			DistributionPlotType distPlotType, NodeValueListOrder order,
			NodeValueListOrderBy orderBy) throws IOException,
			InterruptedException {
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");
		Log.infoSep();
		Log.info("Sequentially plotting Distributions and / or NodeValueLists");
		Log.info("");

		// generate plots
		List<Plot> plots = new LinkedList<Plot>();

		// iterate over metrics and create plots
		for (AggregatedMetric m : initBatch.getMetrics().getList()) {
			String metric = m.getName();
			Log.infoSep();
			Log.info("Plotting metric " + metric);

			// generate distribution plots
			if (plotDistributions) {
				for (AggregatedDistribution d : m.getDistributions().getList()) {
					String distribution = d.getName();
					Log.info("\tplotting distribution '" + distribution + "'");

					// check what to plot
					boolean plotDist = false;
					boolean plotCdf = false;
					switch (distPlotType) {
					case distOnly:
						plotDist = true;
						break;
					case cdfOnly:
						plotCdf = true;
						break;
					case distANDcdf:
						plotDist = true;
						plotCdf = true;
						break;
					}

					// generate normal plots
					if (plotDist) {
						PlotData[] dPlotData = new PlotData[batches.length];
						for (int i = 0; i < batches.length; i++) {
							dPlotData[i] = PlotData.get(distribution, metric,
									style, title + " @ " + timestamps[i], type);
						}
						Plot p = new Plot(dstDir,
								PlotFilenames.getDistributionPlot(metric,
										distribution),
								PlotFilenames.getDistributionGnuplotScript(
										metric, distribution), distribution
										+ " (" + type + ")", dPlotData);

						// disable datetime for distribution plot
						p.setPlotDateTime(false);

						// add to plots
						plots.add(p);
					}

					// generate cdf plots
					if (plotCdf) {
						PlotData[] dPlotDataCDF = new PlotData[batches.length];
						for (int i = 0; i < batches.length; i++) {
							PlotData cdfPlotData = PlotData.get(distribution,
									metric, style, title + " @ "
											+ timestamps[i], type);
							cdfPlotData.setPlotAsCdf(true);
							dPlotDataCDF[i] = cdfPlotData;
						}
						Plot p = new Plot(dstDir,
								PlotFilenames.getDistributionCdfPlot(metric,
										distribution),
								PlotFilenames.getDistributionCdfGnuplotScript(
										metric, distribution), "CDF of "
										+ distribution + " (" + type + ")",
								dPlotDataCDF);

						// disable datetime for distribution plot
						p.setPlotDateTime(false);

						// add to plots
						plots.add(p);
					}
				}
			}

			// generate nodevaluelist plots
			if (plotNodeValues) {
				for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
					String nodevaluelist = n.getName();
					Log.info("\tplotting nodevaluelist '" + nodevaluelist + "'");

					// generate normal plots
					PlotData[] nPlotData = new PlotData[batches.length];
					for (int i = 0; i < batches.length; i++) {
						PlotData plotData = PlotData.get(nodevaluelist, metric,
								style, title + " @ " + timestamps[i], type);
						nPlotData[i] = plotData;
					}

					Plot nPlot = new Plot(dstDir,
							PlotFilenames.getNodeValueListPlot(metric,
									nodevaluelist),
							PlotFilenames.getNodeValueListGnuplotScript(metric,
									nodevaluelist), nodevaluelist + " (" + type
									+ ")", nPlotData);

					// disable datetime for nodevaluelist plot
					nPlot.setPlotDateTime(false);

					// set nvl sort options
					nPlot.setNodeValueListOrder(order);
					nPlot.setNodeValueListOrderBy(orderBy);

					// add to plots
					plots.add(nPlot);
				}
			}
		}

		// generate custom distribution plots
		if (customDistributionPlots != null) {
			if (!customDistributionPlots.isEmpty()) {
				Log.infoSep();
				Log.info("Plotting Custom-Distribution-Plots:");
				for (PlotConfig pc : customDistributionPlots) {
					String name = pc.getName();
					if (name == null)
						continue;
					Log.info("\tplotting '" + name + "'");

					// check for invalid values
					String[] tempValues = pc.getValues();
					String[] tempDomains = pc.getDomains();
					ArrayList<String> valuesList = new ArrayList<String>();
					ArrayList<String> domainsList = new ArrayList<String>();

					for (int i = 0; i < tempValues.length; i++) {
						String v = tempValues[i];
						String d = tempDomains[i];

						if (d.equals(Config
								.get("CUSTOM_PLOT_DOMAIN_STATISTICS"))
								|| d.equals(Config
										.get("CUSTOM_PLOT_DOMAIN_REUNTIMES"))) {
							Log.warn("invalid value '" + tempDomains[i] + "."
									+ tempValues[i]
									+ "' in distribution plot '" + name + "'");
						} else {
							valuesList.add(v);
							domainsList.add(d);
						}
					}

					// only take over valid values
					String[] values = valuesList.toArray(new String[0]);
					String[] domains = domainsList.toArray(new String[0]);

					int valuesCount = values.length;

					// check what to plot
					boolean plotDist = false;
					boolean plotCdf = false;

					if (pc.getDistPlotType() != null) {
						switch (pc.getDistPlotType()) {
						case distOnly:
							plotDist = true;
							break;
						case cdfOnly:
							plotCdf = true;
							break;
						case distANDcdf:
							plotDist = true;
							plotCdf = true;
							break;
						}
					} else {
						plotDist = true;
					}

					// gather plot data
					PlotData[] data = null;
					PlotData[] dataCdf = null;

					if (plotDist)
						data = new PlotData[valuesCount * batches.length];
					if (plotCdf)
						dataCdf = new PlotData[valuesCount * batches.length];

					// gather plot data
					// example: distributions d1, d2
					// -> data[] = { d1(0), d2(0), d1(1), d2(1), ... }
					// where d1(x) is the plotdata of d1 at timestamp x
					for (int i = 0; i < batches.length; i++) {
						for (int j = 0; j < valuesCount; j++) {
							if (plotDist)
								data[i * valuesCount + j] = PlotData.get(
										values[j], domains[j], style,
										domains[j] + "." + values[j] + " @ "
												+ timestamps[i], type);
							if (plotCdf) {
								PlotData dCdf = PlotData.get(values[j],
										domains[j], style, domains[j] + "."
												+ values[j] + " @ "
												+ timestamps[i], type);
								dCdf.setPlotAsCdf(true);
								dataCdf[i * valuesCount + j] = dCdf;
							}
						}
					}

					// create normal plot
					if (plotDist) {
						Plot p = new Plot(dstDir,
								PlotFilenames.getDistributionPlot(name),
								PlotFilenames
										.getDistributionGnuplotScript(name),
								name + " (" + type + ")", data);

						// set data quantity
						p.setDataQuantity(values.length);

						// disable datetime for distribution plot
						p.setPlotDateTime(false);

						// add to plots
						plots.add(p);
					}

					// create cdf plot
					if (plotCdf) {
						Plot pCdf = new Plot(dstDir,
								PlotFilenames.getDistributionCdfPlot(name),
								PlotFilenames
										.getDistributionCdfGnuplotScript(name),
								"CDF of " + name + " (" + type + ")", dataCdf);

						// set data quantity
						pCdf.setDataQuantity(values.length);

						// disable datetime for distribution plot
						pCdf.setPlotDateTime(false);

						// add to plots
						plots.add(pCdf);
					}
				}
			}
		}

		// generate custom nodevaluelist plots
		if (customNodeValueListPlots != null) {
			if (!customNodeValueListPlots.isEmpty()) {
				Log.infoSep();
				Log.info("Plotting Custom-NodeValueList-Plots:");
				for (PlotConfig pc : customNodeValueListPlots) {
					String name = pc.getName();
					if (name == null)
						continue;
					Log.info("\tplotting '" + name + "'");

					// check for invalid values
					String[] tempValues = pc.getValues();
					String[] tempDomains = pc.getDomains();
					ArrayList<String> valuesList = new ArrayList<String>();
					ArrayList<String> domainsList = new ArrayList<String>();

					for (int i = 0; i < tempValues.length; i++) {
						String v = tempValues[i];
						String d = tempDomains[i];

						if (d.equals(Config
								.get("CUSTOM_PLOT_DOMAIN_STATISTICS"))
								|| d.equals(Config
										.get("CUSTOM_PLOT_DOMAIN_REUNTIMES"))) {
							Log.warn("invalid value '" + tempDomains[i] + "."
									+ tempValues[i]
									+ "' in distribution plot '" + name + "'");
						} else {
							valuesList.add(v);
							domainsList.add(d);
						}
					}

					// only take over valid values
					String[] values = valuesList.toArray(new String[0]);
					String[] domains = domainsList.toArray(new String[0]);

					int valuesCount = values.length;

					// gather plot data
					PlotData[] data = new PlotData[batches.length
							* values.length];

					// example: distributions d1, d2
					// -> data[] = { d1(0), d2(0), d1(1), d2(1), ... }
					// where d1(x) is the plotdata of d1 at timestamp x
					for (int i = 0; i < batches.length; i++) {
						for (int j = 0; j < valuesCount; j++) {
							data[i * valuesCount + j] = PlotData.get(values[j],
									domains[j], style,
									domains[j] + "." + values[j] + " @ "
											+ timestamps[i], type);
						}
					}

					// create plot
					Plot p = new Plot(dstDir,
							PlotFilenames.getNodeValueListPlot(name),
							PlotFilenames.getNodeValueListGnuplotScript(
									"custom.nvl", name), name + " (" + type
									+ ")", data);

					// disable datetime for nodevaluelist plot
					p.setPlotDateTime(false);

					// set nvl sort options
					p.setNodeValueListOrder(pc.getOrder());
					p.setNodeValueListOrderBy(pc.getOrderBy());

					// add to plots
					plots.add(p);
				}
			}
		}

		// write headers
		for (Plot p : plots) {
			p.writeScriptHeader();
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

			// append data to plots
			for (Plot p : plots) {
				for (int j = 0; j < p.getDataQuantity(); j++) {
					p.addDataSequentially(tempBatch);
				}
			}

			// free resources
			tempBatch = null;
			System.gc();
		}

		// close and execute plot scripts
		for (Plot p : plots) {
			p.close();
			p.execute();
		}
	}

	/** Plot statistics **/
	private static void plotStatistics(AggregatedBatch[] batchData,
			AggregatedValueList values, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("Plotting values:");
		for (String value : SeriesStats.statisticsToPlot) {
			if (values.getNames().contains(value)) {
				Log.info("\tplotting '" + value + "'");

				// get plot data
				PlotData valuePlotData = PlotData.get(value,
						Config.get("CUSTOM_PLOT_DOMAIN_STATISTICS"), style,
						title, type);

				// create plot
				Plot valuePlot = new Plot(dstDir, PlotFilenames.getValuesPlot(
						Config.get("PREFIX_STATS_PLOT"), value),
						PlotFilenames.getValuesGnuplotScript(
								Config.get("PREFIX_STATS_PLOT"), value), value
								+ " (" + type + ")",
						new PlotData[] { valuePlotData });

				// write header
				valuePlot.writeScriptHeader();

				// append data
				valuePlot.addData(batchData);

				// close and execute
				valuePlot.close();
				valuePlot.execute();
			}
		}
	}

	/** Plots metric values **/
	private static void plotMetricValues(AggregatedBatch[] batchData,
			AggregatedMetricList metrics, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {

		// init list for plots
		List<Plot> plots = new LinkedList<Plot>();

		// generate single plots
		for (AggregatedMetric m : metrics.getList()) {
			String metric = m.getName();
			Log.infoSep();
			Log.info("Plotting metric " + metric);
			for (AggregatedValue v : m.getValues().getList()) {
				String value = v.getName();
				Log.info("\tplotting '" + value + "'");

				// get plot data
				PlotData valuePlotData = PlotData.get(value, m.getName(),
						style, metric + "-" + title, type);

				// create plot
				plots.add(new Plot(dstDir, PlotFilenames.getValuesPlot(metric,
						value), PlotFilenames.getValuesGnuplotScript(metric,
						value), value + " (" + type + ")",
						new PlotData[] { valuePlotData }));
			}
		}

		/*
		 * COMBINED PLOTS
		 */
		ArrayList<String> values = new ArrayList<String>();

		for (AggregatedMetric m : metrics.getList()) {
			for (AggregatedValue v : m.getValues().getList()) {
				if (!values.contains(v.getName()))
					values.add(v.getName());
			}
		}

		// list of values, which all have an own list of metrics
		ArrayList<ArrayList<String>> valuesList = new ArrayList<ArrayList<String>>(
				values.size());

		for (int i = 0; i < values.size(); i++) {
			valuesList.add(i, new ArrayList<String>());
		}

		// for each value add metric that has the value
		for (AggregatedMetric m : metrics.getList()) {
			for (AggregatedValue v : m.getValues().getList()) {
				int index = values.indexOf(v.getName());
				valuesList.get(index).add(m.getName());
			}
		}

		for (int i = 0; i < valuesList.size(); i++) {
			ArrayList<String> metricsList = valuesList.get(i);
			String value = values.get(i);
			if (metricsList.size() > 1) {
				// gather plot data
				PlotData[] valuePlotDatas = new PlotData[metricsList.size()];
				for (int j = 0; j < metricsList.size(); j++) {
					String metric = metricsList.get(j);
					valuePlotDatas[j] = PlotData.get(value, metric, style,
							metric, type);
				}

				// create plot
				plots.add(new Plot(dstDir, PlotFilenames
						.getCombinationPlot(value), PlotFilenames
						.getCombinationGnuplotScript(value), value + " ("
						+ type + ")", valuePlotDatas));
			}
		}

		for (Plot p : plots) {
			// write header
			p.writeScriptHeader();

			// append data
			p.addData(batchData);

			// close and execute
			p.close();
			p.execute();
		}
		plots = null;
	}

	/** Plots metric runtimes **/
	private static void plotMetricRuntimes(AggregatedBatch[] batchData,
			AggregatedRunTimeList metricRuntimes, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
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
					Config.get("CUSTOM_PLOT_DOMAIN_RUNTIMES"), style, runtime
							+ "-" + title, type);
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
			metRuntimeSinglePlot.writeScriptHeader();
			metRuntimeSinglePlotCDF.writeScriptHeader();

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
		metricRuntimesPlot.writeScriptHeader();
		metricRuntimesPlotCDF.writeScriptHeader();

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
			ArrayList<String> y, String dstDir, String title, PlotStyle style,
			PlotType type) throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("Plotting General-Runtimes:");
		PlotData[] genRuntimes = new PlotData[y.size()];
		int index = 0;
		for (String gen : y) {
			Log.info("\tplotting '" + gen + "'");
			genRuntimes[index] = PlotData.get(gen,
					Config.get("CUSTOM_PLOT_DOMAIN_RUNTIMES"), style, gen + "-"
							+ title, type);
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
		generalRuntimesPlot.writeScriptHeader();
		generalRuntimesPlotCDF.writeScriptHeader();

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

	/** Plot custom runtime plots **/
	private static void plotCustomRuntimes(AggregatedBatch[] batchData,
			ArrayList<PlotConfig> customPlots, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("Plotting Custom-Runtime-Plots:");
		for (PlotConfig pc : customPlots) {
			String name = pc.getName();
			if (name == null)
				continue;
			Log.info("\tplotting '" + name + "'");
			String[] values = pc.getValues();
			String[] domains = pc.getDomains();
			boolean plotAsCdf = pc.isPlotAsCdf();
			String plotFilename = name;
			String scriptFilename;
			String plotTitle;
			if (plotAsCdf) {
				plotFilename += Config.get("PLOT_DELIMITER")
						+ Config.get("PLOT_DISTRIBUTION_CDF");
				scriptFilename = PlotFilenames
						.getRuntimesGnuplotScriptCDF(name);
				plotTitle = "CDF of ";
			} else {
				scriptFilename = PlotFilenames.getRuntimesGnuplotScript(name);
				plotTitle = "";
			}
			plotTitle += name + " (" + type + ")";

			// gather plot data
			PlotData[] plotData = new PlotData[values.length];
			for (int i = 0; i < plotData.length; i++) {
				plotData[i] = PlotData.get(values[i], domains[i], style,
						values[i] + "-" + title, type);
			}

			// create plot
			Plot p = new Plot(dstDir, plotFilename, scriptFilename, plotTitle,
					plotData);

			// write script header
			p.writeScriptHeader();

			// add data
			if (plotAsCdf)
				p.addDataFromRuntimesAsCDF(batchData);
			else
				p.addData(batchData);

			// close and execute
			p.close();
			p.execute();
		}
	}

}
