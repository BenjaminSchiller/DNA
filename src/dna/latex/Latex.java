package dna.latex;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.SeriesData;
import dna.util.Log;

/**
 * This class provides methods to export series or parts of a series into a
 * latex-file.
 * 
 * @author Rwilmes
 * @date 24.11.2014
 */
public class Latex {

	public static void test(SeriesData s, String dstDir, String filename)
			throws IOException {
		// log
		Log.info("Exporting series '" + s.getName() + "' at '" + s.getDir()
				+ "' to '" + dstDir + filename + "'");

		// init
		AggregatedBatch initBatch = s.getAggregation().getBatches()[0];

		TexFile file = new TexFile(dstDir, filename);

		file.writeHeader();
		file.writeLine(TexUtils.section("Series "
				+ s.getName().replace("_", "\\textunderscore ")));
		file.writeLine("The series "
				+ s.getName().replace("_", "\\textunderscore is ")
				+ " located in " + dstDir.replace("_", "\\textunderscore ")
				+ ". It contains " + s.getAggregation().getBatches().length
				+ " batches.");
		file.writeLine();
		file.writeCommentBlock("chapters");

		// write statistics
		file.include(TexUtils.getStatisticsChapter(dstDir, initBatch));

		// write general runtimes
		file.include(TexUtils.getGeneralRuntimesChapter(dstDir, initBatch));

		// write metric runtimes
		file.include(TexUtils.getMetricRuntimesChapter(dstDir, initBatch));

		// write metrics
		for (AggregatedMetric m : initBatch.getMetrics().getList()) {
			file.include(TexUtils.getMetricChapter(dstDir, m));
		}

		file.writeLine();

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
