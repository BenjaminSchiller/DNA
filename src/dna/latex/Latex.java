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

	public static void writeTexFromTo(SeriesData s, String dstDir,
			String filename, String plotDir, long from, long to, long stepsize,
			PlottingConfig pconfig) throws IOException {
		TexConfig config = new TexConfig(dstDir, s.getDir(), plotDir,
				new PlotFlag[] { PlotFlag.plotAll }, TableFlag.Average);
		config.setOutputInterval(from, to, stepsize);
		Latex.writeTex(s, dstDir, filename, config, pconfig);
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

		// read single values
		AggregatedBatch[] batchData = new AggregatedBatch[batches.length];
		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			if (zippedRuns)
				ZipReader.readFileSystem = ZipWriter
						.createAggregationFileSystem(srcDir);
			if (zippedBatches)
				batchData[i] = AggregatedBatch.readFromSingleFile(tempDir,
						timestamp, Dir.delimiter,
						BatchReadMode.readOnlySingleValues);
			else
				batchData[i] = AggregatedBatch.read(
						Dir.getBatchDataDir(tempDir, timestamp), timestamp,
						BatchReadMode.readOnlySingleValues);
			if (zippedRuns) {
				ZipReader.readFileSystem.close();
				ZipReader.readFileSystem = null;
			}
		}

		// init
		AggregatedBatch initBatch = batchData[0];
		TexFile file = new TexFile(dstDir, filename);

		// WRITE PREAMBLE
		file.writePreamble(dstDir);

		// start with content
		file.writeLine(TexUtils.chapter("Series "
				+ s.getName().replace("_", "\\textunderscore ")));
		file.writeLine("The series "
				+ s.getName().replace("_", "\\textunderscore ")
				+ " is located in " + dstDir.replace("_", "\\textunderscore ")
				+ ". It contains " + s.getAggregation().getBatches().length
				+ " batches.");
		file.writeLine();
		file.writeCommentBlock("chapters");

		// write statistics
		if (config.isIncludeStatistics())
			file.include(TexUtils.generateStatisticsChapter(dstDir, plotDir,
					initBatch, batchData, config, pconfig));

		if (config.isIncludeRuntimes()) {
			// write general runtimes
			file.include(TexUtils.generateGeneralRuntimesChapter(dstDir,
					plotDir, initBatch, batchData, config, pconfig));

			// write metric runtimes
			file.include(TexUtils.generateMetricRuntimesChapter(dstDir,
					plotDir, initBatch, batchData, config, pconfig));
		}

		// write metrics
		if (config.isIncludeMetrics()) {
			for (AggregatedMetric m : initBatch.getMetrics().getList()) {
				if ((config.isIncludeDistributions() && m.getDistributions()
						.size() > 0)
						|| (config.isIncludeMetricValues() && m.getValues()
								.size() > 0)
						|| (config.isIncludeNodeValueLists() && m
								.getNodeValues().size() > 0)) {
					file.include(TexUtils.generateMetricChapter(dstDir,
							plotDir, s, m, batchData, config, pconfig));
				}
			}
		}

		file.writeLine();

		// close document
		file.closeAndEnd();

		// log
		Log.info("Latex-Output finished!");
	}
}
