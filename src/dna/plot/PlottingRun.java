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
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.BatchData;
import dna.series.data.RunData;
import dna.series.data.SeriesData;
import dna.util.Config;
import dna.util.Log;
import dna.util.Memory;

/**
 * Plotting class which holds static methods for run plotting.
 * 
 * @author Rwilmes
 * @date 30.09.2014
 */
public class PlottingRun {

	/**
	 * Plots the runs of the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig that configures the plotting process.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		// craft config
		PlottingConfig config = new PlottingConfig(PlotFlag.plotAll);

		// call method
		PlottingRun.plot(seriesData, dstDir, config);
	}

	/**
	 * Plots the runs of the series to the destination dir.
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
	public static void plot(SeriesData seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		// log output
		Log.infoSep();
		Log.info("plotting " + seriesData.getRuns().size()
				+ " run(s) from series '" + seriesData.getName() + "' to "
				+ dstDir);

		int index = 0;
		for (RunData run : seriesData.getRuns()) {
			Log.info("\tplotting run." + index);
			PlottingRun.plotRun(seriesData, run, index,
					Dir.getRunDataDir(dstDir, index), config);
			index++;
		}

		Log.info("Plotting finished!");
	}

	/**
	 * Plots the runs of the series to the destination dir.
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
	public static void plot(SeriesData seriesData, int index, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		// log output
		Log.infoSep();
		Log.info("plotting " + seriesData.getRuns().size()
				+ " run(s) from series '" + seriesData.getName() + "' to "
				+ dstDir);
		PlottingRun.plotRun(seriesData, seriesData.getRun(index), index,
				dstDir, config);
		Log.info("Plotting finished!");
	}

	/**
	 * Plots the runs of the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param inizes
	 *            Indizes of the runs to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig configuring the plotting process.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData[] seriesData, int[] indizes,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {
		// check if index valid
		boolean valid = true;
		for (int i = 0; i < seriesData.length; i++) {
			if (seriesData[i].getRuns().size() <= indizes[i]) {
				Log.error("index out of bounds. Series '"
						+ seriesData[i].getName() + "' doesnt have run."
						+ indizes[i]);
				valid = false;
			}
		}

		if (valid) {
			// log output and gather runs
			RunData[] runs = new RunData[seriesData.length];
			Log.infoSep();
			Log.info("plotting runs:");
			for (int i = 0; i < seriesData.length; i++) {
				Log.info("\trun." + indizes[i] + " from series '"
						+ seriesData[i].getName() + "'");
				runs[i] = seriesData[i].getRun(indizes[i]);
			}
			PlottingRun.plotRuns(seriesData, runs, indizes, dstDir, config);
			Log.info("Plotting finished!");
		}
	}

	/**
	 * Plots a specific run.
	 * 
	 * @param series
	 *            Source series.
	 * @param run
	 *            Run to be plotted.
	 * @param index
	 *            Index of the run.
	 * @param dstDir
	 *            Destination directory.
	 * @param config
	 *            PlottingConfig configuring the plotting process.
	 * @throws IOException
	 */
	private static void plotRun(SeriesData series, RunData run, int index,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {
		// read values from config
		long plotFrom = config.getPlotFrom();
		long plotTo = config.getPlotTo();
		long stepsize = config.getStepsize();
		boolean intervalByIndex = config.isIntervalByIndex();

		// ignore config plottype and set to average!
		PlotType type = PlotType.average;
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
		String tempDir = Dir.getRunDataDir(series.getDir(), index);

		String[] batches = Dir.getBatchesFromTo(tempDir, plotFrom, plotTo,
				stepsize, intervalByIndex);
		double timestamps[] = new double[batches.length];
		for (int i = 0; i < batches.length; i++) {
			timestamps[i] = Dir.getTimestamp(batches[i]);
		}

		// read single values
		BatchData[] batchData = new BatchData[batches.length];
		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			batchData[i] = BatchData.readIntelligent(
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

		// set init batch
		BatchData initBatch = batchData[0];

		// replace wildcards and remove unnecessary plots
		config.checkCustomPlotConfigs(new BatchData[] { initBatch });

		// plot statistics
		if (config.isPlotStatistics()) {
			// plot custom statistic plots
			if (config.getCustomStatisticPlots() != null) {
				if (config.getCustomStatisticPlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-Statistic-Plots:");
					PlottingUtils.plotCustomValuePlots(batchData,
							series.getDir(), config.getCustomStatisticPlots(),
							dstDir, title, style, type, valueSortMode,
							valueSortList, config.getTimestampMap());
				}
			}
		}

		// plot custom value plots
		if (config.isPlotCustomValues()) {
			Log.infoSep();
			Log.info("Plotting Custom-Value-Plots:");
			PlottingUtils.plotCustomValuePlots(batchData, series.getDir(),
					config.getCustomValuePlots(), dstDir, title, style, type,
					valueSortMode, valueSortList, config.getTimestampMap());
		}

		// plot runtimes
		if (config.isPlotRuntimes()) {
			// plot custom runtimes
			PlottingUtils.plotCustomRuntimes(batchData, series.getDir(),
					config.getCustomRuntimePlots(), dstDir, title, style, type,
					valueSortMode, valueSortList, config.getTimestampMap());
		}

		// plot metric values
		if (config.isPlotMetricValues()) {
			PlottingUtils.plotMetricValues(batchData, series.getDir(),
					initBatch, dstDir, title, style, type, valueSortMode,
					valueSortList, config.getCustomMetricValuePlots(),
					config.getCustomValuePlots(), config.getTimestampMap());

			// plot custom metric value plots
			if (config.getCustomMetricValuePlots() != null) {
				if (config.getCustomMetricValuePlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-MetricValue-Plots:");
					PlottingUtils.plotCustomValuePlots(batchData,
							series.getDir(),
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
					plotNodeValues, initBatch, series.getDir(), batches,
					timestamps, config.getCustomDistributionPlots(),
					config.getCustomNodeValueListPlots(), series.getDir(),
					tempDir, dstDir, title, style, type, distPlotType, order,
					orderBy);
	}

	private static void plotRuns(SeriesData[] seriesData, RunData[] runs,
			int[] indizes, String dstDir, PlottingConfig config)
			throws IOException, InterruptedException {
		// create dir
		(new File(dstDir)).mkdirs();

		long plotFrom = config.getPlotFrom();
		long plotTo = config.getPlotTo();
		long stepsize = config.getStepsize();
		boolean intervalByIndex = config.isIntervalByIndex();

		// ignore config plottype and set to average!
		PlotType type = PlotType.average;
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
		String tempRunDir = Dir.getRunDataDir(seriesData[0].getDir(),
				indizes[0]);
		String[] batches = Dir.getBatchesFromTo(tempRunDir, plotFrom, plotTo,
				stepsize, intervalByIndex);

		for (int i = 0; i < seriesData.length; i++) {
			String tempDir = Dir.getRunDataDir(seriesData[i].getDir(),
					indizes[i]);
			String[] tempBatches = Dir.getBatchesFromTo(tempDir, plotFrom,
					plotTo, stepsize, intervalByIndex);
			if (tempBatches.length > batches.length)
				batches = tempBatches;
		}

		double timestamps[] = new double[batches.length];
		for (int j = 0; j < batches.length; j++) {
			timestamps[j] = Dir.getTimestamp(batches[j]);
		}

		@SuppressWarnings("unchecked")
		ArrayList<Long>[] seriesTimestamps = new ArrayList[seriesData.length];

		// check what batches each series possesses
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData sd = seriesData[i];
			seriesTimestamps[i] = new ArrayList<Long>();

			for (double t : timestamps) {
				for (int j = 0; j < sd.getRun(indizes[i]).getBatches().size(); j++) {
					long timestamp = sd.getRun(indizes[i]).getBatches().get(j)
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
		BatchData[] initBatches = new BatchData[seriesData.length];

		// read init batches
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData series = seriesData[i];
			String tempDir = Dir.getRunDataDir(series.getDir(), indizes[i]);
			long timestamp = series.getRuns().get(indizes[i]).getBatches()
					.get(0).getTimestamp();

			initBatches[i] = BatchData.readIntelligent(
					Dir.getBatchDataDir(tempDir, timestamp), timestamp,
					BatchReadMode.readOnlySingleValues);
		}

		// replace wildcards and remove unnecessary plots
		config.checkCustomPlotConfigs(initBatches);

		// plot single value plots
		if (plotStatistics || plotMetricValues || plotRuntimes
				|| plotCustomValues)
			PlottingUtils.plotSingleValuePlots(seriesData, indizes, dstDir,
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
					indizes, dstDir, batches, timestamps, initBatches,
					seriesTimestamps, plotDistributions,
					config.getCustomDistributionPlots(), plotNodeValues,
					config.getCustomNodeValueListPlots(), zippedBatches,
					zippedRuns, distPlotType, order, orderBy, type, style,
					valueSortMode, valueSortList);

	}
}
