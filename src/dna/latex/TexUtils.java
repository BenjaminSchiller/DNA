package dna.latex;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.SeriesData;

/**
 * Utility class for tex.
 */
public class TexUtils {

	// static tex strings
	public static final String newline = "\\\\";
	public static final String commentIdentifier = "%";
	public static final String chapterDirectory = "chapters";
	public static final String texSuffix = ".tex";

	// dir
	public static final String statisticsFilename = "statistics";
	public static final String generalRuntimesFilename = "generalruntimes";
	public static final String metricRuntimesFilename = "metricruntimes";

	// comments
	public static final String beginOfDocument = "begin of document";
	public static final String endOfDocument = "end of document";

	// static contents
	public static final String statistics = "Statistics";
	public static final String generalRuntimes = "General Runtimes";
	public static final String metricRuntimes = "Metric Runtimes";
	public static final String institute = "Dresden University of Technology";
	public static final String[] valueDesciptions = { "Timestamp", "Avg",
			"Min", "Max", "Median", "Var", "Var\\_low", "Var\\_high",
			"Conf\\_low", "Conf\\_high" };

	// commands
	public static String cmd(String command, String value) {
		return "\\" + command + "{" + value + "}";
	}

	public static String textBf(String value) {
		return cmd("textbf", value);
	}

	public static String chapter(String value) {
		return cmd("chapter", value);
	}

	public static String section(String value) {
		return cmd("section", value);
	}

	public static String subsection(String value) {
		return cmd("subsection", value);
	}

	public static String subsubsection(String value) {
		return cmd("subsubsection", value);
	}

	public static String begin(String value) {
		return cmd("begin", value);
	}

	public static String end(String value) {
		return cmd("end", value);
	}

	public static String include(String value) {
		return cmd("include", value);
	}

	public static TexFile getStatisticsChapter(String dstDir,
			AggregatedBatch initBatch, AggregatedBatch[] batchData)
			throws IOException {
		// write statistics
		TexFile stats = new TexFile(dstDir + TexUtils.chapterDirectory
				+ Dir.delimiter, TexUtils.statisticsFilename
				+ TexUtils.texSuffix);

		if (initBatch.getValues().size() > 0) {
			stats.writeCommentBlock(TexUtils.statistics);
			stats.writeLine(TexUtils.section(TexUtils.statistics));
			stats.writeLine();
			for (AggregatedValue v : initBatch.getValues().getList()) {
				stats.writeLine(TexUtils.subsection(v.getName()));
				stats.writeLine(v.getName() + " is a statistic.");
				stats.writeLine();
				stats.writeCommentBlock("value table of " + v.getName());

				// init table
				TexTable table = new TexTable(stats, TexUtils.valueDesciptions);

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getValues().getNames().contains(v.getName())) {
						table.addBlankRow(TexUtils.valueDesciptions.length - 1,
								b.getTimestamp());
					} else {
						table.addRow(
								b.getValues().get(v.getName()).getValues(),
								b.getTimestamp());
					}
				}

				// close table
				table.close();
				stats.writeLine();
			}
			stats.writeLine();
		}
		stats.close();
		return stats;
	}

	public static TexFile getGeneralRuntimesChapter(String dstDir,
			AggregatedBatch initBatch, AggregatedBatch[] batchData)
			throws IOException {
		// write general runtimes
		TexFile genR = new TexFile(dstDir + TexUtils.chapterDirectory
				+ Dir.delimiter, TexUtils.generalRuntimesFilename
				+ TexUtils.texSuffix);

		if (initBatch.getGeneralRuntimes().size() > 0) {
			genR.writeCommentBlock(TexUtils.generalRuntimes);
			genR.writeLine(TexUtils.section(TexUtils.generalRuntimes));
			genR.writeLine();
			for (AggregatedValue v : initBatch.getGeneralRuntimes().getList()) {
				genR.writeLine(TexUtils.subsection(v.getName()));
				genR.writeLine(v.getName() + " is a general runtime.");
				genR.writeLine();
				genR.writeCommentBlock("value table of " + v.getName());

				// init table
				TexTable table = new TexTable(genR, TexUtils.valueDesciptions);

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getGeneralRuntimes().getNames()
							.contains(v.getName())) {
						table.addBlankRow(TexUtils.valueDesciptions.length - 1,
								b.getTimestamp());
					} else {
						table.addRow(b.getGeneralRuntimes().get(v.getName())
								.getValues(), b.getTimestamp());
					}
				}

				// close table
				table.close();
				genR.writeLine();
			}
			genR.writeLine();
		}
		genR.close();
		return genR;
	}

	public static TexFile getMetricRuntimesChapter(String dstDir,
			AggregatedBatch initBatch, AggregatedBatch[] batchData)
			throws IOException {
		// write metric runtimes
		TexFile metR = new TexFile(dstDir + TexUtils.chapterDirectory
				+ Dir.delimiter, TexUtils.metricRuntimesFilename
				+ TexUtils.texSuffix);

		if (initBatch.getMetricRuntimes().size() > 0) {
			metR.writeCommentBlock(TexUtils.metricRuntimes);
			metR.writeLine(TexUtils.section(TexUtils.metricRuntimes));
			metR.writeLine();
			for (AggregatedValue v : initBatch.getMetricRuntimes().getList()) {
				metR.writeLine(TexUtils.subsection(v.getName()));
				metR.writeLine(v.getName() + " is a metric runtime.");
				metR.writeLine();
				metR.writeCommentBlock("value table of " + v.getName());

				// init table
				TexTable table = new TexTable(metR, TexUtils.valueDesciptions);

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getMetricRuntimes().getNames().contains(v.getName())) {
						table.addBlankRow(TexUtils.valueDesciptions.length - 1,
								b.getTimestamp());
					} else {
						table.addRow(b.getMetricRuntimes().get(v.getName())
								.getValues(), b.getTimestamp());
					}
				}

				// close table
				table.close();
				metR.writeLine();

			}
			metR.writeLine();
		}
		metR.close();
		return metR;
	}

	public static TexFile getMetricChapter(String dstDir, SeriesData s,
			AggregatedMetric m, AggregatedBatch[] batchData) throws IOException {
		TexFile mFile = new TexFile(dstDir + TexUtils.chapterDirectory
				+ Dir.delimiter, m.getName() + TexUtils.texSuffix);
		mFile.writeCommentBlock(m.getName());
		mFile.writeMetric(s, m, batchData);
		mFile.close();
		return mFile;
	}
}
