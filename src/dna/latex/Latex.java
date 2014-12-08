package dna.latex;

import java.io.File;
import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedValue;
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

	public static void test(SeriesData s, String dstDir, String filename,
			TexConfig config) throws IOException {
		// log
		Log.info("Exporting series '" + s.getName() + "' at '" + s.getDir()
				+ "' to '" + dstDir + filename + "'");

		long from = 0;
		long to = 10;
		int stepsize = 1;
		boolean intervalByIndex = true;
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");

		// create dir
		(new File(dstDir)).mkdirs();

		// gather relevant batches
		String tempDir = Dir.getAggregationDataDir(s.getDir());
		String[] batches = Dir.getBatchesFromTo(tempDir, from, to, stepsize,
				intervalByIndex);
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

		// init
		AggregatedBatch initBatch = batchData[0];

		TexFile file = new TexFile(dstDir, filename);

		file.writeHeader();
		file.writeLine(TexUtils.chapter("Series "
				+ s.getName().replace("_", "\\textunderscore ")));
		file.writeLine("The series "
				+ s.getName().replace("_", "\\textunderscore is ")
				+ " located in " + dstDir.replace("_", "\\textunderscore ")
				+ ". It contains " + s.getAggregation().getBatches().length
				+ " batches.");
		file.writeLine();
		file.writeCommentBlock("chapters");

		// write statistics
		if (config.isIncludeStatistics())
			file.include(TexUtils.generateStatisticsChapter(dstDir, initBatch,
					batchData, config));

		if (config.isIncludeRuntimes()) {
			// write general runtimes
			file.include(TexUtils.generateGeneralRuntimesChapter(dstDir,
					initBatch, batchData, config));

			// write metric runtimes
			file.include(TexUtils.generateMetricRuntimesChapter(dstDir,
					initBatch, batchData, config));
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
					file.include(TexUtils.generateMetricChapter(dstDir, s, m,
							batchData, config));
				}
			}
		}

		file.writeLine();

		// close document
		file.closeAndEnd();
	}

	public static void addStatisticsChapter(TexFile file, String dstDir,
			AggregatedBatch initBatch) throws IOException {
		// write statistics
		TexFile stats = new TexFile(dstDir + TexUtils.chapterDirectory
				+ Dir.delimiter, TexUtils.statisticsFilename
				+ TexUtils.texSuffix);

		if (initBatch.getValues().size() > 0) {
			stats.writeCommentBlock(TexUtils.statistics);
			stats.writeLine(TexUtils.subsection(TexUtils.statistics));
			stats.writeLine();
			for (AggregatedValue v : initBatch.getValues().getList()) {
				stats.writeLine(TexUtils.subsubsection(v.getName()));
				stats.writeLine(v.getName() + " is a statistic.");
				stats.writeLine();
			}
			stats.writeLine();
		}
		stats.close();
		file.include(stats);
	}

	// public static void addGeneralRuntimesChapter(TexFile file, String dstDir,
	// AggregatedBatch initBatch) throws IOException {
	//
	// }

}
