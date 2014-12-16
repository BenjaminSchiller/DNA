package dna.latex;

import java.io.IOException;
import java.util.ArrayList;

import dna.io.filesystem.Dir;
import dna.latex.TexTable.TableFlag;
import dna.plot.PlotConfig;
import dna.plot.PlottingConfig;
import dna.plot.PlottingUtils;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.SeriesData;

/**
 * Utility class for tex.
 */
public class TexUtils {

	// static tex strings
	public static final String tab = "\t";
	public static final String newline = "\\\\";
	public static final String commentIdentifier = "%";
	public static final String chapterDirectory = "chapters";
	public static final String texSuffix = ".tex";
	public static final String plotLabelPrefix = "plot:";

	// dir
	public static final String statisticsFilename = "statistics";
	public static final String generalRuntimesFilename = "generalruntimes";
	public static final String metricRuntimesFilename = "metricruntimes";
	public static final String titlePageFilename = "titlepage";
	public static final String headerFilename = "header";

	// comments
	public static final String beginOfDocument = "begin of document";
	public static final String endOfDocument = "end of document";

	// static contents
	public static final String statistics = "Statistics";
	public static final String generalRuntimes = "General Runtimes";
	public static final String metricRuntimes = "Metric Runtimes";
	public static final String institute = "Dresden University of Technology";
	public static final String[] valueDesciptions = { "Timestamp", "Average",
			"Min", "Max", "Median", "Var", "VarLow", "VarUp", "ConfLow",
			"ConfUp" };

	// commands
	public static String cmd(String command, String value) {
		return "\\" + command + "{" + value + "}";
	}

	public static String cmd(String command) {
		return "\\" + command;
	}

	public static String option(String option, String value) {
		return option(option + "=" + value);
	}

	public static String option(String option) {
		return "[" + option + "]";
	}

	public static String argument(String argument) {
		return "{" + argument + "}";
	}

	public static String textBf(String value) {
		return cmd("textbf", value);
	}

	public static String textSc(String value) {
		return cmd("textsc", value);
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

	public static String pagenumbering(String value) {
		return cmd("pagenumbering", value);
	}

	public static String setcounter(String value) {
		return cmd("setcounter", "page") + "{" + value + "}";
	}

	public static String centering() {
		return cmd("centering");
	}

	public static String caption(String value) {
		return cmd("caption", value);
	}

	public static String label(String value) {
		return cmd("label", value);
	}

	public static String includeGraphics(String path) {
		return cmd("includegraphics", path);
	}

	public static String ref(String value) {
		return cmd("ref", value);
	}

	public static String includeGraphics(String path, double scale) {
		return cmd("includegraphics") + " " + option("scale", "" + scale)
				+ " {" + path + "}";
	}

	public static String vertDistance(int points) {
		return option(points + "ex");
	}

	public static String large() {
		return cmd("Large");
	}

	public static String beginFigure(String option) {
		return begin("figure") + " " + option(option);
	}

	public static String endFigure() {
		return end("figure");
	}

	public static String getPlotLabel(String plot) {
		return TexUtils.plotLabelPrefix + plot;
	}

	public static TexFile generateStatisticsChapter(String dstDir,
			String plotDir, AggregatedBatch initBatch,
			AggregatedBatch[] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
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

				// plots
				ArrayList<PlotConfig> plots = pconfig.getCustomStatisticPlots();
				ArrayList<PlotConfig> fits = TexUtils
						.getCustomStatisticPlotFits(v.getName(), plots);

				// add ref line
				if (fits.size() > 0) {
					String refs;
					if (fits.size() > 1)
						refs = "See plots: ";
					else
						refs = "See plot: ";
					boolean first = true;
					for (PlotConfig pc : fits) {
						String ref = TexUtils.ref(TexUtils.getPlotLabel(pc
								.getFilename()));
						if (first) {
							refs += ref;
							first = false;
						} else {
							refs += ", " + ref;
						}
					}
					refs += ".";
					stats.writeLine(refs);
					stats.writeLine();

					// add plots that contain the value
					stats.writeCommentBlock("plots containing " + v.getName());
					stats.writeLine();
					for (PlotConfig pc : fits) {
						stats.includeFigure(v.getName(), plotDir,
								pc.getFilename());
					}
				}

				// values
				stats.writeCommentBlock("value table of " + v.getName());

				// select description
				String[] tableDescrArray = TexUtils.selectDescription(config);

				// init table
				TexTable table = new TexTable(stats, tableDescrArray);

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getValues().getNames().contains(v.getName())) {
						table.addBlankRow(tableDescrArray.length - 1,
								b.getTimestamp());
					} else {
						// select values
						double[] selectedValues = TexUtils.selectValues(b
								.getValues().get(v.getName()), config, false);

						// add row
						table.addRow(selectedValues, b.getTimestamp());
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

	public static TexFile generateGeneralRuntimesChapter(String dstDir,
			AggregatedBatch initBatch, AggregatedBatch[] batchData,
			TexConfig config) throws IOException {
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

				// select description
				String[] tableDescrArray = TexUtils.selectDescription(config);

				// init table
				TexTable table = new TexTable(genR, tableDescrArray);

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getGeneralRuntimes().getNames()
							.contains(v.getName())) {
						table.addBlankRow(tableDescrArray.length - 1,
								b.getTimestamp());
					} else {
						// select values
						double[] selectedValues = TexUtils.selectValues(b
								.getGeneralRuntimes().get(v.getName()), config,
								false);

						// add row
						table.addRow(selectedValues, b.getTimestamp());
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

	public static TexFile generateMetricRuntimesChapter(String dstDir,
			AggregatedBatch initBatch, AggregatedBatch[] batchData,
			TexConfig config) throws IOException {
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

				// select description
				String[] tableDescrArray = TexUtils.selectDescription(config);

				// init table
				TexTable table = new TexTable(metR, tableDescrArray);

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getMetricRuntimes().getNames().contains(v.getName())) {
						table.addBlankRow(tableDescrArray.length - 1,
								b.getTimestamp());
					} else {
						// select values
						double[] selectedValues = TexUtils.selectValues(b
								.getMetricRuntimes().get(v.getName()), config,
								false);

						// add row
						table.addRow(selectedValues, b.getTimestamp());
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

	public static TexFile generateMetricChapter(String dstDir, SeriesData s,
			AggregatedMetric m, AggregatedBatch[] batchData, TexConfig config)
			throws IOException {
		TexFile mFile = new TexFile(dstDir + TexUtils.chapterDirectory
				+ Dir.delimiter, m.getName() + TexUtils.texSuffix);
		mFile.writeCommentBlock(m.getName());
		mFile.writeMetric(s, m, batchData, config);
		mFile.close();
		return mFile;
	}

	/**
	 * Returns a table description for the given tex-config. Note that the first
	 * column will be labeled as "timestamp" by default.
	 **/
	public static String[] selectDescription(TexConfig config) {
		// check what data to add in table
		ArrayList<String> tableDescr = new ArrayList<String>();
		tableDescr.add("Timestamp");
		String[] tableDescrArray = null;

		boolean done = false;
		for (TableFlag tf : config.getTableFlags()) {
			if (!done) {
				switch (tf) {
				case all:
					tableDescrArray = TexUtils.valueDesciptions;
					done = true;
					break;
				default:
					tableDescr.add(tf.toString());
					break;
				}
			}
		}

		if (!done)
			tableDescrArray = tableDescr.toArray(new String[0]);

		// return
		return tableDescrArray;
	}

	/** Selects the respective values from v defined by the tex-config. **/
	public static double[] selectValuesFromDistribution(AggregatedValue v,
			TexConfig config) {
		return TexUtils.selectValues(v, config, true);
	}

	/** Selects the respective values from v defined by the tex-config. **/
	public static double[] selectValuesFromNodeValueList(AggregatedValue v,
			TexConfig config) {
		return TexUtils.selectValues(v, config, false);
	}

	/** Selects the respective values from v defined by the tex-config. **/
	public static double[] selectValues(AggregatedValue v, TexConfig config,
			boolean distribution) {
		ArrayList<Double> selectedValues = new ArrayList<Double>();
		double[] selectedValuesArray = null;

		// if distribution, add offset of 1
		int offset = 0;
		if (distribution)
			offset = 1;

		boolean done = false;
		for (TableFlag tf : config.getTableFlags()) {
			if (!done) {
				switch (tf) {
				case all:
					selectedValuesArray = v.getValues();
					done = true;
					break;
				case Average:
					selectedValues.add(v.getValues()[0 + offset]);
					break;
				case Min:
					selectedValues.add(v.getValues()[1 + offset]);
					break;
				case Max:
					selectedValues.add(v.getValues()[2 + offset]);
					break;
				case Median:
					selectedValues.add(v.getValues()[3 + offset]);
					break;
				case Var:
					selectedValues.add(v.getValues()[4 + offset]);
					break;
				case VarLow:
					selectedValues.add(v.getValues()[5 + offset]);
					break;
				case VarUp:
					selectedValues.add(v.getValues()[6 + offset]);
					break;
				case ConfLow:
					selectedValues.add(v.getValues()[7 + offset]);
					break;
				case ConfUp:
					selectedValues.add(v.getValues()[8 + offset]);
					break;
				}
			}
		}
		if (!done) {
			selectedValuesArray = new double[selectedValues.size()];
			for (int j = 0; j < selectedValuesArray.length; j++) {
				selectedValuesArray[j] = selectedValues.get(j);
			}
		}

		// return
		return selectedValuesArray;
	}

	/** Generates and writes the titlepage. **/
	public static TexFile generateTitlepage(String dir, String filename)
			throws IOException {
		TexFile titlepage = new TexFile(dir, filename);
		System.out.println("titlepage at " + dir + "   " + filename);
		titlepage.writeLine(TexUtils.begin("titlepage"));
		titlepage.writeLine(TexUtils.tab + TexUtils.centering());
		titlepage.writeLine(TexUtils.tab
				+ TexUtils.includeGraphics("images/dna-logo-v5", 0.25)
				+ TexUtils.newline + " " + TexUtils.vertDistance(20));
		titlepage.writeLine(TexUtils.tab
				+ TexUtils.textSc(TexUtils.large() + " "
						+ TexUtils.cmd("Project")) + TexUtils.newline + " "
				+ TexUtils.vertDistance(2));
		titlepage.writeLine(TexUtils.tab
				+ TexUtils.textSc(TexUtils.large() + " "
						+ TexUtils.cmd("DocumentType")) + TexUtils.newline
				+ " " + TexUtils.vertDistance(30));
		titlepage.writeLine(TexUtils.tab
				+ TexUtils.textSc(TexUtils.cmd("AuthorName"))
				+ TexUtils.newline + " " + TexUtils.vertDistance(1));
		titlepage.writeLine(TexUtils.tab
				+ TexUtils.textSc(TexUtils.cmd("Date")) + TexUtils.newline
				+ " " + TexUtils.vertDistance(1));
		titlepage.writeLine(TexUtils.tab
				+ TexUtils.textSc(TexUtils.cmd("Institute")) + TexUtils.newline
				+ " " + TexUtils.vertDistance(1));
		titlepage.writeLine(TexUtils.tab + TexUtils.cmd("vfill"));
		titlepage.writeLine(TexUtils.end("titlepage"));
		titlepage.writeLine();
		titlepage.writeLine(TexUtils.cmd("currentpdfbookmark",
				"Table of Contents") + TexUtils.argument("content"));
		titlepage.writeLine(TexUtils.cmd("tableofcontents"));

		// close & return
		titlepage.close();
		return titlepage;
	}

	/** Returns the custom statistic plots that contain the given statistic. **/
	public static ArrayList<PlotConfig> getCustomStatisticPlotFits(
			String statistic, ArrayList<PlotConfig> plots) {
		ArrayList<PlotConfig> fits = new ArrayList<PlotConfig>();
		for (PlotConfig p : plots) {
			boolean finished = false;
			String[] values = p.getValues();
			String[] domains = p.getDomains();

			for (int i = 0; i < values.length && !finished; i++) {
				String dom = domains[i];
				String val = values[i];

				// if function
				if (dom.equals(PlotConfig.customPlotDomainFunction))
					continue;

				if (dom.equals(PlotConfig.customPlotDomainExpression)) {
					// if expression
					dom = PlottingUtils.getDomainFromExpression(val,
							p.getGeneralDomain());
					val = PlottingUtils.getValueFromExpression(val);
				}

				// if regular plot
				if (!dom.equals(PlotConfig.customPlotDomainStatistics))
					continue;

				// if contains value, add plot to
				if (val.equals(statistic)) {
					if (!fits.contains(p)) {
						fits.add(p);
						finished = true;
					}
				}
			}
		}
		return fits;
	}

}
