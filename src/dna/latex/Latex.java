package dna.latex;

import java.io.File;
import java.io.IOException;

import dna.io.ZipReader;
import dna.io.ZipWriter;
import dna.io.filesystem.Dir;
import dna.latex.TexTable.TableFlag;
import dna.plot.Plotting;
import dna.plot.PlottingConfig;
import dna.plot.PlottingConfig.PlotFlag;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedMetric;
import dna.series.data.SeriesData;
import dna.util.Config;
import dna.util.Log;

/**
 * This class provides methods to export series or parts of a series into a
 * latex-file.
 * 
 * @author Rwilmes
 * @date 24.11.2014
 */
public class Latex {

	public static void writeTexAndPlot(SeriesData s, String dstDir,
			String filename) throws IOException, InterruptedException {
		Latex.writeTexAndPlotFromTo(s, dstDir, filename, 0, Long.MAX_VALUE, 1);
	}

	public static void writeTexAndPlotFromTo(SeriesData s, String dstDir,
			String filename, long from, long to, long stepsize)
			throws IOException, InterruptedException {
		// craft config
		PlottingConfig pconfig = new PlottingConfig(PlotFlag.plotAll);
		pconfig.setPlotInterval(from, to, stepsize);
		String plotDir = "plots/";

		// plot
		Plotting.plot(s, dstDir + plotDir, pconfig);
		Log.infoSep();

		// tex
		Latex.writeTexFromTo(s, dstDir, filename, plotDir, from, to, stepsize,
				pconfig);
	}

	public static void writeTexAndPlot(SeriesData s, String dstDir,
			String filename, TexConfig config) throws IOException,
			InterruptedException {
		// craft config
		PlottingConfig pconfig = new PlottingConfig(PlotFlag.plotAll);

		// plot and tex
		Latex.writeTexAndPlot(s, dstDir, filename, config, pconfig);
	}

	public static void writeTexAndPlot(SeriesData s, String dstDir,
			String filename, TexConfig config, PlottingConfig pconfig)
			throws IOException, InterruptedException {
		// plot
		Plotting.plot(s, dstDir + "plots/", pconfig);
		Log.infoSep();

		// tex
		Latex.writeTex(s, dstDir, filename, config, pconfig);
	}

	public static void writeTexFromTo(SeriesData s, String dstDir,
			String filename, String plotDir, long from, long to, long stepsize,
			PlottingConfig pconfig) throws IOException {
		TexConfig config = new TexConfig(dstDir, s.getDir(), plotDir,
				new PlotFlag[] { PlotFlag.plotAll }, TableFlag.Average);
		config.setOutputInterval(from, to, stepsize);
		Latex.writeTex(s, dstDir, filename, config, pconfig);
	}

	/**
	 * Plots all series iteratively and creates one LaTeX document containing
	 * each series as a separate chapter.
	 * 
	 * @param series
	 *            SeriesData objects that will be plottet and added.
	 * @param dstDir
	 *            Destination directory.
	 * @param filename
	 *            Filename of the tex-preamble-file.
	 * @param config
	 *            TexConfig configuring the tex-output.
	 * @param pconfig
	 *            PlottingConfig configuring the plots.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void writeTexAndPlot(SeriesData[] series, String dstDir,
			String filename, TexConfig config, PlottingConfig pconfig)
			throws IOException, InterruptedException {
		// PLOT
		String[] plotDirs = new String[series.length];
		for (int i = 0; i < series.length; i++) {
			// plot to absolute dir
			Plotting.plot(series[i], config.getPlotDir() + series[i].getName() + Dir.delimiter, pconfig);
			
			// set relative plot directories
			plotDirs[i] = "plots/" + series[i].getName() + Dir.delimiter;
		}

		// TEX
		Latex.writeTex(series, dstDir, filename, plotDirs, config, pconfig);
	}

	public static void writeTex(SeriesData[] series, String dstDir,
			String filename, String[] plotDirs, TexConfig config,
			PlottingConfig pconfig) throws IOException, InterruptedException {
		// print series'
		String buff = "";
		String[] srcDirs = new String[series.length];
		for (int i = 0; i < series.length; i++) {
			if (i == 0)
				buff += series[i].getName();
			else
				buff += ", " + series[i].getName();
			// get source dirs
			srcDirs[i] = series[i].getDir();
		}

		// log
		Log.infoSep();
		Log.info("Exporting series " + buff + " to '" + dstDir + filename
				+ "'.");

		// get entries from config
		long from = config.getFrom();
		long to = config.getTo();
		long stepsize = config.getStepsize();
		boolean intervalByIndex = config.isIntervalByIndex();
		boolean zippedBatches = false;
		boolean zippedRuns = false;
		if (Config.get("GENERATION_AS_ZIP").equals("batches"))
			zippedBatches = true;
		if (Config.get("GENERATION_AS_ZIP").equals("runs"))
			zippedRuns = true;
		// create dir
		(new File(dstDir)).mkdirs();

		// copy logo
		TexUtils.copyLogo(dstDir);

		// gather relevant batches and timestamps
		String[][] batches = new String[series.length][];
		double[][] timestamps = new double[series.length][];

		for (int i = 0; i < series.length; i++) {
			String tempDir = Dir.getAggregationDataDir(series[i].getDir());
			if (zippedRuns) {
				ZipReader.readFileSystem = ZipWriter
						.createAggregationFileSystem(series[i].getDir());
				tempDir = Dir.delimiter;
			}
			batches[i] = Dir.getBatchesFromTo(tempDir, from, to, stepsize,
					intervalByIndex);
			timestamps[i] = new double[batches[i].length];
			for (int j = 0; j < batches[i].length; j++) {
				timestamps[i][j] = Dir.getTimestamp(batches[i][j]);
			}
			if (zippedRuns) {
				ZipReader.readFileSystem.close();
				ZipReader.readFileSystem = null;
			}
		}

		// CREATE TEX FILE
		TexFile file = new TexFile(dstDir, filename);

		// WRITE PREAMBLE
		file.writePreamble(dstDir);

		// ADD SERIES CHAPTERS
		for (int i = 0; i < series.length; i++) {
			file.addSeriesChapter(series[i], series[i].getDir(), dstDir,
					plotDirs[i], batches[i], config, pconfig, zippedRuns,
					zippedBatches);
		}

		// CLOSE FILE AND END
		file.closeAndEnd();
		Log.info("Latex-Output finished!");
	}

	public static void writeTex(SeriesData s, String dstDir, String filename,
			TexConfig config, PlottingConfig pconfig) throws IOException {
		String srcDir = s.getDir();

		// log
		Log.info("Exporting series '" + s.getName() + "' at '" + srcDir
				+ "' to '" + dstDir + filename + "'");
		String plotDir = config.getPlotDir();
		long from = config.getFrom();
		long to = config.getTo();
		long stepsize = config.getStepsize();
		boolean intervalByIndex = config.isIntervalByIndex();
		boolean zippedBatches = false;
		boolean zippedRuns = false;
		if (Config.get("GENERATION_AS_ZIP").equals("batches"))
			zippedBatches = true;
		if (Config.get("GENERATION_AS_ZIP").equals("runs"))
			zippedRuns = true;

		// create dir
		(new File(dstDir)).mkdirs();

		// copy logo
		TexUtils.copyLogo(dstDir);

		// gather relevant batches
		String tempDir = Dir.getAggregationDataDir(srcDir);
		if (zippedRuns) {
			ZipReader.readFileSystem = ZipWriter
					.createAggregationFileSystem(srcDir);
			tempDir = Dir.delimiter;
		}
		String[] batches = Dir.getBatchesFromTo(tempDir, from, to, stepsize,
				intervalByIndex);
		double timestamps[] = new double[batches.length];
		for (int i = 0; i < batches.length; i++) {
			timestamps[i] = Dir.getTimestamp(batches[i]);
		}
		if (zippedRuns) {
			ZipReader.readFileSystem.close();
			ZipReader.readFileSystem = null;
		}

		// INIT TEX FILE
		TexFile file = new TexFile(dstDir, filename);

		// WRITE PREAMBLE
		file.writePreamble(dstDir);

		// ADD SERIES CHAPTER
		file.addSeriesChapter(s, srcDir, dstDir, plotDir, batches, config,
				pconfig, zippedRuns, zippedBatches);

		// close document
		file.closeAndEnd();

		// log
		Log.info("Latex-Output finished!");
	}
}
