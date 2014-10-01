package dna.plot;

import java.io.File;
import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.plot.PlottingConfig.PlotFlag;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.series.aggdata.AggregatedBatch;
import dna.series.data.BatchData;
import dna.series.data.RunData;
import dna.series.data.SeriesData;
import dna.util.Config;
import dna.util.Log;

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
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
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
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
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
			PlottingRun.plotRun(seriesData, run, index, dstDir + index + "/",
					config);
			index++;
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
			String dstDir, PlottingConfig config) throws IOException, InterruptedException {
		// read values from config
		long plotFrom = config.getPlotFrom();
		long plotTo = config.getPlotTo();
		long stepsize = config.getStepsize();
		boolean intervalByIndex = config.isIntervalByIndex();
		PlotType type = config.getPlotType();
		PlotStyle style = config.getPlotStyle();
		NodeValueListOrder order = config.getNvlOrder();
		NodeValueListOrderBy orderBy = config.getNvlOrderBy();
		DistributionPlotType distPlotType = config.getDistPlotType();
		String title = series.getName();
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");
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
			if (singleFile)
				batchData[i] = BatchData.readFromSingleFile(
						Dir.getBatchDataDir(tempDir, timestamp), timestamp,
						Dir.delimiter, true);
			else
				batchData[i] = BatchData.read(
						Dir.getBatchDataDir(tempDir, timestamp), timestamp,
						true);
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
//		config.checkCustomPlotConfigs(new AggregatedBatch[] { initBatch });
		// TODO: custom plot config check for common batchdata
		
		// replace wildcards and remove unnecessary plots
		config.checkCustomPlotConfigs(new BatchData[] { initBatch });
		
		
	}

}
