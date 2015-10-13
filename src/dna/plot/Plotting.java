package dna.plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dna.io.filesystem.Dir;
import dna.plot.PlottingConfig.PlotFlag;
import dna.plot.PlottingConfig.ValueSortMode;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
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
		// log output
		Log.infoSep();
		Log.info("plotting data from " + seriesData.length + " series to "
				+ dstDir);

		// check if aggregation exists
		boolean noAggregation = false;
		for (SeriesData sd : seriesData) {
			if (sd.getAggregation() == null) {
				noAggregation = true;
				break;
			}
		}

		// if more than 1 series, call multiple plot method
		if (seriesData.length > 1) {
			if (noAggregation) {
				Log.warn("no aggregation found, plotting run.0 instead");
				PlottingRun.plot(seriesData, new int[seriesData.length],
						dstDir, config);
			} else {
				Plotting.plotFromToMultipleSeries(seriesData, dstDir, config);
			}
		}

		// if single series, call single plot method
		if (seriesData.length == 1) {
			if (noAggregation) {
				Log.warn("no aggregation found, plotting runs");
				PlottingRun.plot(seriesData[0], dstDir, config);
			} else {
				Plotting.plotFromToSingleSeries(seriesData[0], dstDir, config);
			}
		}

		// if no series, print out warning
		if (seriesData.length < 1)
			Log.error("Plotting called without a series to plot.");
		else
			Log.info("Plotting finished!");

	}

	/**
	 * Plots multiple series.
	 * 
	 * @param series
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory.
	 * @param config
	 *            PlottingConfig controlling the plot.
	 * @throws IOException
	 *             Thrown by the writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	private static void plotFromToMultipleSeries(SeriesData[] seriesData,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {
		// create dir
		(new File(dstDir)).mkdirs();

		long plotFrom = config.getPlotFrom();
		long plotTo = config.getPlotTo();
		long stepsize = config.getStepsize();
		boolean intervalByIndex = config.isIntervalByIndex();
		PlotType type = config.getPlotType();
		PlotStyle style = config.getPlotStyle();
		ValueSortMode valueSortMode = config.getValueSortMode();
		String[] valueSortList = config.getValueSortList();
		NodeValueListOrder order = config.getNvlOrder();
		NodeValueListOrderBy orderBy = config.getNvlOrderBy();
		DistributionPlotType distPlotType = config.getDistPlotType();

		boolean zippedRuns = false;
		boolean zippedBatches = false;
		if (Config.get("GENERATION_AS_ZIP").equals("runs"))
			zippedRuns = true;
		if (Config.get("GENERATION_AS_ZIP").equals("batches"))
			zippedBatches = true;

		boolean plotDistributions = config.isPlotDistributions();
		boolean plotNodeValues = config.isPlotNodeValueLists();

		boolean plotStatistics = config.isPlotStatistics();
		boolean plotMetricValues = config.isPlotMetricValues();
		boolean plotCustomValues = config.isPlotCustomValues();
		boolean plotRuntimes = config.isPlotRuntimes();

		// gather relevant batches
		String[] batches;
		batches = Dir.getBatchesFromTo(
				Dir.getAggregationDataDir(seriesData[0].getDir()), plotFrom,
				plotTo, stepsize, intervalByIndex);

		for (int i = 0; i < seriesData.length; i++) {
			String[] tempBatches = Dir.getBatchesFromTo(
					Dir.getAggregationDataDir(seriesData[i].getDir()),
					plotFrom, plotTo, stepsize, intervalByIndex);
			if (tempBatches.length > batches.length)
				batches = tempBatches;
		}

		double timestamps[] = new double[batches.length];
		for (int j = 0; j < batches.length; j++) {
			timestamps[j] = Dir.getTimestamp(batches[j]);
		}

		// list series'
		for (int i = 0; i < seriesData.length; i++) {
			Log.info("\t'" + seriesData[i].getName() + "'");
		}

		@SuppressWarnings("unchecked")
		ArrayList<Long>[] seriesTimestamps = new ArrayList[seriesData.length];

		// check what batches each series possesses
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData sd = seriesData[i];
			seriesTimestamps[i] = new ArrayList<Long>();

			for (double t : timestamps) {
				for (int j = 0; j < sd.getAggregation().getBatches().length; j++) {
					long timestamp = sd.getAggregation().getBatches()[j]
							.getTimestamp();
					if (timestamp == t)
						seriesTimestamps[i].add(timestamp);
				}
			}
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

		// array of the initbatch of each series
		AggregatedBatch[] initBatches = new AggregatedBatch[seriesData.length];

		// read init batches
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData series = seriesData[i];
			long timestamp = series.getAggregation().getBatches()[0]
					.getTimestamp();
			initBatches[i] = AggregatedBatch.readIntelligent(Dir
					.getBatchDataDir(
							Dir.getAggregationDataDir(series.getDir()),
							timestamp), timestamp, BatchReadMode.readAllValues);
		}

		// replace wildcards and remove unnecessary plots
		config.checkCustomPlotConfigs(initBatches);

		// plot single value plots
		if (plotStatistics || plotMetricValues || plotRuntimes
				|| plotCustomValues)
			PlottingUtils.plotSingleValuePlots(seriesData, null, dstDir,
					batches, timestamps, initBatches, plotStatistics,
					config.getCustomStatisticPlots(), plotMetricValues,
					config.getCustomMetricValuePlots(), plotCustomValues,
					config.getCustomValuePlots(), plotRuntimes,
					config.getCustomRuntimePlots(), zippedBatches, zippedRuns,
					type, style, valueSortMode, valueSortList,
					config.getTimestampMap());

		// plot distribution and nodevaluelist plots
		if (plotDistributions || plotNodeValues)
			PlottingUtils.plotDistributionAndNodeValueListPlots(seriesData,
					null, dstDir, batches, timestamps, initBatches,
					seriesTimestamps, plotDistributions,
					config.getCustomDistributionPlots(), plotNodeValues,
					config.getCustomNodeValueListPlots(), zippedBatches,
					zippedRuns, distPlotType, order, orderBy, type, style,
					valueSortMode, valueSortList);
	}

	/**
	 * Plots a single series.
	 * 
	 * @param series
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory.
	 * @param config
	 *            PlottingConfig controlling the plot.
	 * @throws IOException
	 *             Thrown by the writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	private static void plotFromToSingleSeries(SeriesData series,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {
		// read values from config
		long plotFrom = config.getPlotFrom();
		long plotTo = config.getPlotTo();
		long stepsize = config.getStepsize();
		boolean intervalByIndex = config.isIntervalByIndex();
		PlotType type = config.getPlotType();
		PlotStyle style = config.getPlotStyle();
		ValueSortMode valueSortMode = config.getValueSortMode();
		String[] valueSortList = config.getValueSortList();
		NodeValueListOrder order = config.getNvlOrder();
		NodeValueListOrderBy orderBy = config.getNvlOrderBy();
		DistributionPlotType distPlotType = config.getDistPlotType();
		String title = series.getName();
		boolean plotDistributions = config.isPlotDistributions();
		boolean plotNodeValues = config.isPlotNodeValueLists();

		// create dir
		(new File(dstDir)).mkdirs();

		// gather relevant batches
		String tempDir = Dir.getAggregationDataDir(series.getDir());
		String[] batches = Dir.getBatchesFromTo(tempDir, plotFrom, plotTo,
				stepsize, intervalByIndex);
		double timestamps[] = new double[batches.length];
		for (int i = 0; i < batches.length; i++) {
			timestamps[i] = Dir.getTimestamp(batches[i]);
		}

		// read single values
		AggregatedBatch[] batchData = new AggregatedBatch[batches.length];
		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			batchData[i] = AggregatedBatch.readIntelligent(
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

		// replace wildcards and remove unnecessary plots
		config.checkCustomPlotConfigs(new AggregatedBatch[] { initBatch });

		// plot statistics
		if (config.isPlotStatistics()) {
			// plot custom statistic plots
			if (config.getCustomStatisticPlots() != null) {
				if (config.getCustomStatisticPlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-Statistic-Plots:");
					PlottingUtils.plotCustomValuePlots(batchData,
							config.getCustomStatisticPlots(), dstDir, title,
							style, type, valueSortMode, valueSortList,
							config.getTimestampMap());
				}
			}
		}

		// plot custom value plots
		if (config.isPlotCustomValues()) {
			Log.infoSep();
			Log.info("Plotting Custom-Value-Plots:");
			PlottingUtils.plotCustomValuePlots(batchData,
					config.getCustomValuePlots(), dstDir, title, style, type,
					valueSortMode, valueSortList, config.getTimestampMap());
		}

		// plot runtimes
		if (config.isPlotRuntimes()) {
			// plot custom runtimes
			PlottingUtils.plotCustomRuntimes(batchData,
					config.getCustomRuntimePlots(), dstDir, title, style, type,
					valueSortMode, valueSortList, config.getTimestampMap());
		}

		// plot metric values
		if (config.isPlotMetricValues()) {
			PlottingUtils.plotMetricValues(batchData, initBatch, dstDir, title,
					style, type, valueSortMode, valueSortList,
					config.getCustomMetricValuePlots(),
					config.getCustomValuePlots(), config.getTimestampMap());

			// plot custom metric value plots
			if (config.getCustomMetricValuePlots() != null) {
				if (config.getCustomMetricValuePlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-MetricValue-Plots:");
					PlottingUtils.plotCustomValuePlots(batchData,
							config.getCustomMetricValuePlots(), dstDir, title,
							style, type, valueSortMode, valueSortList,
							config.getTimestampMap());
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
			PlottingUtils.plotDistributionsAndNodeValues(plotDistributions,
					plotNodeValues, initBatch, batches, timestamps,
					config.getCustomDistributionPlots(),
					config.getCustomNodeValueListPlots(), series.getDir(),
					tempDir, dstDir, title, style, type, distPlotType, order,
					orderBy);
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
	 *            (Optional) Flags that define which will be plotted. If empty
	 *            all flags will be set.
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
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param timestampFrom
	 *            Beginning of the timestamp interval to be plotted.
	 * @param timestampTo
	 *            Ending of the timestamp interval to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromTo(SeriesData seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepsize,
			PlottingConfig config) throws IOException, InterruptedException {
		Plotting.plotFromTo(new SeriesData[] { seriesData }, dstDir,
				timestampFrom, timestampTo, stepsize, config);
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
	 *            (Optional) Flags that define which will be plotted. If empty
	 *            all flags will be set.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromTo(SeriesData[] seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepsize,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config;
		if (flags.length == 0)
			config = new PlottingConfig(PlotFlag.plotAll);
		else
			config = new PlottingConfig(flags);

		// set plotting interval
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
	 * @param indexFrom
	 *            Index of the first batch to be plotted.
	 * @param indexTo
	 *            Index of the last batch to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param flags
	 *            (Optional) Flags that define which will be plotted. If empty
	 *            all flags will be set.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromToByIndex(SeriesData seriesData, String dstDir,
			int indexFrom, int indexTo, int stepsize, PlotFlag... flags)
			throws IOException, InterruptedException {
		Plotting.plotFromToByIndex(new SeriesData[] { seriesData }, dstDir,
				indexFrom, indexTo, stepsize, flags);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param indexFrom
	 *            Index of the first batch to be plotted.
	 * @param indexTo
	 *            Index of the last batch to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromToByIndex(SeriesData seriesData, String dstDir,
			int indexFrom, int indexTo, int stepsize, PlottingConfig config)
			throws IOException, InterruptedException {
		Plotting.plotFromToByIndex(new SeriesData[] { seriesData }, dstDir,
				indexFrom, indexTo, stepsize, config);
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
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromTo(SeriesData[] seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepsize,
			PlottingConfig config) throws IOException, InterruptedException {
		// craft config
		PlottingConfig tempConfig = config.clone(PlotFlag.plotAll);
		tempConfig.setPlotInterval(timestampFrom, timestampTo, stepsize);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, tempConfig);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param indexFrom
	 *            Index of the first batch to be plotted.
	 * @param indexTo
	 *            Index of the last batch to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromToByIndex(SeriesData[] seriesData,
			String dstDir, int indexFrom, int indexTo, int stepsize,
			PlottingConfig config) throws IOException, InterruptedException {
		// craft config
		PlottingConfig tempConfig = config.clone(PlotFlag.plotAll);
		tempConfig.setPlotIntervalByIndex(indexFrom, indexTo, stepsize);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, tempConfig);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param indexFrom
	 *            Index of the first batch to be plotted.
	 * @param indexTo
	 *            Index of the last batch to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param flags
	 *            (Optional) Flags that define which will be plotted. If empty
	 *            all flags will be set.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromToByIndex(SeriesData[] seriesData,
			String dstDir, int indexFrom, int indexTo, int stepsize,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config;
		if (flags.length == 0)
			config = new PlottingConfig(PlotFlag.plotAll);
		else
			config = new PlottingConfig(flags);

		// set plotting interval
		config.setPlotIntervalByIndex(indexFrom, indexTo, stepsize);

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
	 * series to the destination dir.
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
	public static void plotRunsSeparately(SeriesData seriesData, String dstDir,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config;
		if (flags.length == 0)
			config = new PlottingConfig(PlotFlag.plotAll);
		else
			config = new PlottingConfig(flags);

		// call method
		Plotting.plotRunsSeparately(seriesData, dstDir, config);
	}

	/**
	 * Plots the runs of the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig configuring the plotting process.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRunsSeparately(SeriesData seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		PlottingRun.plot(seriesData, dstDir, config);
	}

	/**
	 * Plots the runs of the series' to the destination dir.
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
	public static void plotRunsSeparately(SeriesData[] seriesData,
			String dstDir, PlotFlag... flags) throws IOException,
			InterruptedException {
		// craft config
		PlottingConfig config;
		if (flags.length == 0)
			config = new PlottingConfig(PlotFlag.plotAll);
		else
			config = new PlottingConfig(flags);

		// call method
		Plotting.plotRunsSeparately(seriesData, dstDir, config);
	}

	/**
	 * Plots the runs of the series' to the destination dir.
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
	public static void plotRunsSeparately(SeriesData[] seriesData,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {

		// calculate max number of runs
		int runs = seriesData[0].getRuns().size();

		for (int i = 1; i < seriesData.length; i++) {
			if (seriesData[i].getRuns().size() > runs)
				runs = seriesData[i].getRuns().size();
		}

		// iterate over runs
		for (int i = 0; i < runs; i++) {
			ArrayList<SeriesData> series = new ArrayList<SeriesData>();

			// iterate over series
			for (int j = 0; j < seriesData.length; j++) {
				// check if series contains run with index i
				if (seriesData[j].getRuns().size() > i) {
					series.add(seriesData[j]);
				}
			}

			// create arrays with valid series'
			SeriesData[] tempSeries = series.toArray(new SeriesData[series
					.size()]);
			int[] tempIndizes = new int[tempSeries.length];
			for (int k = 0; k < tempIndizes.length; k++)
				tempIndizes[k] = i;

			// call plotting method
			PlottingRun.plot(tempSeries, tempIndizes,
					Dir.getRunDataDir(dstDir, i), config);
		}
	}

	/**
	 * Plots the run of the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param index
	 *            Index of the run to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRun(SeriesData seriesData, int index, String dstDir,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config;
		if (flags.length == 0)
			config = new PlottingConfig(PlotFlag.plotAll);
		else
			config = new PlottingConfig(flags);

		// call method
		Plotting.plotRun(seriesData, index, dstDir, config);
	}

	/**
	 * Plots the run of the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param index
	 *            Index of the run to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig configuring the plotting process.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRun(SeriesData seriesData, int index, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		PlottingRun.plot(seriesData, index, dstDir, config);
	}

	/**
	 * Plots the run of the series' to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param index
	 *            Index of the run to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRun(SeriesData[] seriesData, int index,
			String dstDir, PlotFlag... flags) throws IOException,
			InterruptedException {
		// craft config
		PlottingConfig config;
		if (flags.length == 0)
			config = new PlottingConfig(PlotFlag.plotAll);
		else
			config = new PlottingConfig(flags);

		// call method
		Plotting.plotRun(seriesData, index, dstDir, config);
	}

	/**
	 * Plots the run of the series' to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param index
	 *            Index of the run to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig configuring the plotting process.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRun(SeriesData[] seriesData, int index,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {
		int[] indizes = new int[seriesData.length];
		for (int i = 0; i < indizes.length; i++)
			indizes[i] = index;
		PlottingRun.plot(seriesData, indizes, dstDir, config);
	}

	/**
	 * Plots all runs from a single series to destination dir.
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
	public static void plotRunsCombined(SeriesData seriesData, String dstDir,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config;
		if (flags.length == 0)
			config = new PlottingConfig(PlotFlag.plotAll);
		else
			config = new PlottingConfig(flags);

		// call method
		Plotting.plotRunsCombined(seriesData, dstDir, config);
	}

	/**
	 * Plots all runs from a single series to destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig configuring the plotting process.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRunsCombined(SeriesData seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		int runs = seriesData.getRuns().size();
		SeriesData[] series = new SeriesData[runs];
		int[] indizes = new int[runs];
		for (int i = 0; i < series.length; i++) {
			series[i] = seriesData;
			indizes[i] = i;
		}

		// call method
		PlottingRun.plot(series, indizes, dstDir, config);
	}

}
