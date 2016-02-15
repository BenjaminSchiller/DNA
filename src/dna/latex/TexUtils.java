package dna.latex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarFile;

import dna.io.filesystem.Dir;
import dna.latex.TexTable.TableFlag;
import dna.latex.TexTable.TableMode;
import dna.plot.PlotConfig;
import dna.plot.PlottingConfig;
import dna.plot.PlottingUtils;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.SeriesData;
import dna.util.Config;

/**
 * Utility class for tex.
 */
public class TexUtils {

	// static tex strings
	public static final String tab = "\t";
	public static final String newline = "\\\\";
	public static final String commentIdentifier = "%";
	public static final String plotLabelPrefix = "plot:";
	public static final String cdfSuffix = ".CDF";
	public static String logoSrcPath = "logo/versions/";
	public static String logoFilename = "dna-logo-v5";
	public static String logoSuffix = ".png";

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
	public static final String plots = "Plots";
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

	public static String usepackage(String value, String option) {
		return cmd("usepackage") + option(option) + argument(value);
	}

	public static String usepackage(String value) {
		return cmd("usepackage", value);
	}

	public static String chapter(String value) {
		return cmd("chapter", value);
	}

	public static String section(String value) {
		return cmd("section", value.replace("_", "\\textunderscore "));
	}

	public static String subsection(String value) {
		return cmd("subsection", value.replace("_", "\\textunderscore "));
	}

	public static String subsubsection(String value) {
		return cmd("subsubsection", value.replace("_", "\\textunderscore "));
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

	public static String pagestyle(String value) {
		return cmd("pagestyle", value);
	}

	public static String pagenumbering(String value) {
		return cmd("pagenumbering", value);
	}

	public static String automark() {
		return cmd("automark") + option("chapter") + argument("section");
	}

	public static String newcommand() {
		return cmd("newcommand");
	}

	public static String definecolor(String name, String scheme, String value) {
		return cmd("definecolor") + argument(name) + argument(scheme)
				+ argument(value);
	}

	public static String definecolor(String name, String scheme, String value1,
			String value2) {
		return definecolor(name, scheme, value1) + '%' + argument(value2);
	}

	public static String setcounter(String value) {
		return cmd("setcounter", "page") + "{" + value + "}";
	}

	public static String centering() {
		return cmd("centering");
	}

	public static String caption(String value) {
		return cmd("caption", value.replace("_", "\\textunderscore "));
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

	public static String textSubscript(String value) {
		return cmd("textsubscript", value);
	}

	public static String getPlotLabel(String series, String plot) {
		return TexUtils.getPlotLabel(series + Config.get("LATEX_DELIMITER")
				+ plot);
	}

	public static String getPlotLabel(String plot) {
		return TexUtils.plotLabelPrefix + plot;
	}

	/** Generates a combined statistics chapter. **/
	public static TexFile generateStatisticsChapter(String[] seriesNames,
			String dstDir, String plotDir, AggregatedBatch[] initBatches,
			AggregatedBatch[][] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		// write statistics
		TexFile stats = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, TexUtils.statisticsFilename
				+ Config.get("SUFFIX_TEX_FILE"));

		// added plots
		ArrayList<PlotConfig> addedPlots = new ArrayList<PlotConfig>();

		// list values
		ArrayList<String> values = new ArrayList<String>();
		for (AggregatedBatch initBatch : initBatches) {
			for (String v : initBatch.getValues().getNames()) {
				if (!values.contains(v))
					values.add(v);
			}
		}

		// if no values, do nothing
		if (values.size() > 0) {
			stats.writeCommentBlock(TexUtils.statistics);
			stats.writeLine(TexUtils.section(TexUtils.statistics));
			stats.writeLine();

			// for each value
			for (String v : values) {
				// add subsection
				stats.writeLine(TexUtils.subsection(v));
				stats.writeLine(v.replace("_", "\\textunderscore ")
						+ " is a statistic.");
				stats.writeLine();

				// gather fitting plots
				ArrayList<PlotConfig> fits = TexUtils
						.getCustomStatisticPlotFits(v,
								pconfig.getCustomStatisticPlots());

				// add ref line
				if (fits.size() > 0) {
					String refs = TexUtils.getReferenceString(fits, "");
					stats.writeLine(refs);
					stats.writeLine();

					// add plots that contain the value
					for (PlotConfig pc : fits) {
						if (!addedPlots.contains(pc)) {
							addedPlots.add(pc);
						}
					}
				}

				// values
				stats.writeCommentBlock("value table of " + v);

				// check which series has the most batches
				int max = 0;
				for (int i = 1; i < batchData.length; i++) {
					if (batchData[i].length > batchData[max].length)
						max = i;
				}

				// check if combined tables
				if (config.isMultipleSeriesTables()) {
					TableMode mode = config.getTableMode();

					// lists of values to be shown in the tables
					ArrayList<Integer> seriesIndexList = new ArrayList<Integer>();
					ArrayList<TableFlag> flagList = new ArrayList<TableFlag>();

					// MODE = ALTERNATING SERIES
					if (mode.equals(TableMode.alternatingSeries)) {
						// fill lists
						for (TableFlag tf : config.getTableFlags()) {
							for (int i = 0; i < batchData.length; i++) {
								seriesIndexList.add(i);
								flagList.add(tf);
							}
						}
					}

					// MODE = ALTERNATING VALUES
					if (mode.equals(TableMode.alternatingValues)) {
						for (int i = 0; i < batchData.length; i++) {
							for (TableFlag tf : config.getTableFlags()) {
								seriesIndexList.add(i);
								flagList.add(tf);

							}
						}
					}

					// generate tables
					TexUtils.generateCombinedTables(seriesNames, batchData,
							stats, v, PlotConfig.customPlotDomainStatistics,
							seriesIndexList, flagList, max, config);

				} else {
					// else: one table for each flag
					// select description
					String[] tableDescrArray = TexUtils
							.selectDescription(seriesNames);

					// variables for horizontal alignment
					int currentColumns = 0;
					int currentTables = 0;

					// one table for each flag
					for (TableFlag tf : config.getTableFlags()) {
						// init table
						MultiValueTexTable table = new MultiValueTexTable(
								stats, tableDescrArray, config.getDateFormat(),
								config.getScaling(), config.getMapping(), tf);

						// add values
						for (int i = 0; i < batchData[max].length; i++) {
							long timestamp = batchData[max][i].getTimestamp();

							AggregatedValue[] avs = new AggregatedValue[batchData.length];

							for (int j = 0; j < batchData.length; j++) {
								if (batchData[j].length > i) {
									AggregatedBatch b = batchData[j][i];
									if (b.getValues().getNames().contains(v))
										avs[j] = b.getValues().get(v);
									else
										avs[j] = new AggregatedValue(v,
												new double[] { 0.0, 0.0, 0.0,
														0.0, 0.0, 0.0, 0.0,
														0.0, 0.0 });
								}
							}

							table.addDataRow(avs, timestamp);
						}

						// close table
						table.close();

						// check for horizontal alignment
						int t = table.getTableCounter();
						currentColumns += t * tableDescrArray.length;
						currentTables += t;

						// if to large, add blankline and reset
						if ((currentTables + 1) * currentColumns > Config
								.getInt("LATEX_TABLE_MAX_COLUMNS")) {
							stats.writeLine();
							currentColumns = 0;
							currentTables = 0;
						}
					}
				}
			}

			// add plots subsection
			if (addedPlots.size() > 0)
				TexUtils.addPlotsSubsection(stats, "", plotDir, addedPlots);

			stats.writeLine();
		}

		// close and return
		stats.close();
		return stats;
	}

	/** Generates tables to the given series, batches and inputes. **/
	public static void generateCombinedTables(String[] seriesNames,
			AggregatedBatch[][] batchData, TexFile file, String value,
			String domain, ArrayList<Integer> seriesIndexList,
			ArrayList<TableFlag> flagList, int maxSeriesIndex, TexConfig config)
			throws IOException {
		// init
		int maxColumns = Config.getInt("LATEX_TABLE_MAX_COLUMNS");
		int vp = 0;
		int columnsLeft = seriesIndexList.size();

		// while columns left, write tables
		while (columnsLeft > 0) {
			int amountColumns = columnsLeft;
			if (columnsLeft > maxColumns)
				amountColumns = maxColumns;

			// init table description
			String[] tableDescrArray = new String[amountColumns + 1];
			tableDescrArray[0] = "Timestamp";
			int[] valuePointers = new int[amountColumns];

			// fill description array
			for (int i = 1; i < tableDescrArray.length; i++) {
				tableDescrArray[i] = TexUtils.getShortString(flagList.get(vp))
						+ TexUtils.textSubscript(seriesNames[seriesIndexList
								.get(vp)].replace("_", "\\textunderscore "));
				valuePointers[i - 1] = vp;
				vp++;
			}

			// init table
			MultiValueTexTable table = new MultiValueTexTable(file,
					tableDescrArray, config.getDateFormat(),
					config.getScaling(), config.getMapping(), null);

			// add values, iterate over batches
			for (int i = 0; i < batchData[maxSeriesIndex].length; i++) {
				long timestamp = batchData[maxSeriesIndex][i].getTimestamp();

				AggregatedValue[] avs = new AggregatedValue[amountColumns];
				TableFlag[] tableFlags = new TableFlag[amountColumns];

				// iterate over columns
				for (int j = 0; j < amountColumns; j++) {
					int pointer = valuePointers[j];
					int sIndex = seriesIndexList.get(pointer);
					tableFlags[j] = flagList.get(pointer);

					// get data from batches
					if (batchData[sIndex].length > i) {
						AggregatedBatch b = batchData[sIndex][i];

						if (domain
								.equals(PlotConfig.customPlotDomainStatistics)) {
							if (b.getValues().getNames().contains(value))
								avs[j] = b.getValues().get(value);
						} else if (domain
								.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
							if (b.getGeneralRuntimes().getNames()
									.contains(value))
								avs[j] = TexUtils.formatRuntime(b
										.getGeneralRuntimes().get(value));
						} else if (domain
								.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
							if (b.getMetricRuntimes().getNames()
									.contains(value)) {
								avs[j] = TexUtils.formatRuntime(b
										.getMetricRuntimes().get(value));
							}
						} else {
							// check if domain = metric
							if (b.getMetrics().getNames().contains(domain)) {
								if (b.getMetrics().get(domain).getValues()
										.getNames().contains(value)) {
									avs[j] = b.getMetrics().get(domain)
											.getValues().get(value);
								}
							}
						}
					}
				}

				table.addDataRow(avs, timestamp, tableFlags);
			}

			// close table
			table.close();

			// write line
			file.writeLine();

			// remove used columns from counter
			columnsLeft -= amountColumns;
		}
	}

	/** Generates a statistics chapter. **/
	public static TexFile generateStatisticsChapter(String seriesName,
			String dstDir, String plotDir, AggregatedBatch initBatch,
			AggregatedBatch[] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		// write statistics
		TexFile stats = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, seriesName + Config.get("LATEX_DELIMITER")
				+ TexUtils.statisticsFilename + Config.get("SUFFIX_TEX_FILE"));

		// added plots
		ArrayList<PlotConfig> addedPlots = new ArrayList<PlotConfig>();

		// if no values, do nothing
		if (initBatch.getValues().size() > 0) {
			stats.writeCommentBlock(TexUtils.statistics);
			stats.writeLine(TexUtils.section(TexUtils.statistics));
			stats.writeLine();

			// for each value
			for (AggregatedValue v : initBatch.getValues().getList()) {
				// add subsection
				stats.writeLine(TexUtils.subsection(v.getName()));
				stats.writeLine(v.getName().replace("_", "\\textunderscore ")
						+ " is a statistic.");
				stats.writeLine();

				// gather fitting plots
				ArrayList<PlotConfig> fits = TexUtils
						.getCustomStatisticPlotFits(v.getName(),
								pconfig.getCustomStatisticPlots());

				// add ref line
				if (fits.size() > 0) {
					String refs = TexUtils.getReferenceString(fits, seriesName);
					stats.writeLine(refs);
					stats.writeLine();

					// add plots that contain the value
					for (PlotConfig pc : fits) {
						if (!addedPlots.contains(pc)) {
							addedPlots.add(pc);
						}
					}
				}

				// values
				stats.writeCommentBlock("value table of " + v.getName());

				// select description
				String[] tableDescrArray = TexUtils.selectDescription(config);

				// init table
				SingleScalarTexTable table = new SingleScalarTexTable(stats,
						tableDescrArray, config.getDateFormat(),
						config.getScaling(), config.getMapping());

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

			// add plots subsection
			if (addedPlots.size() > 0)
				TexUtils.addPlotsSubsection(stats, seriesName, plotDir,
						addedPlots);

			stats.writeLine();
		}
		stats.close();
		return stats;
	}

	/** Creates a combined general runtimes chapter. **/
	public static TexFile generateGeneralRuntimesChapter(String[] seriesNames,
			String dstDir, String plotDir, AggregatedBatch[] initBatches,
			AggregatedBatch[][] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		// write general runtimes
		TexFile genR = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, TexUtils.generalRuntimesFilename
				+ Config.get("SUFFIX_TEX_FILE"));

		// added plots
		ArrayList<PlotConfig> addedPlots = new ArrayList<PlotConfig>();

		// list values
		ArrayList<String> values = new ArrayList<String>();
		for (AggregatedBatch initBatch : initBatches) {
			for (String v : initBatch.getGeneralRuntimes().getNames()) {
				if (!values.contains(v))
					values.add(v);
			}
		}

		// if no values, do nothing
		if (values.size() > 0) {
			genR.writeCommentBlock(TexUtils.generalRuntimes);
			genR.writeLine(TexUtils.section(TexUtils.generalRuntimes));
			genR.writeLine();

			// check which series has the most batches
			int max = 0;
			for (int i = 1; i < batchData.length; i++) {
				if (batchData[i].length > batchData[max].length)
					max = i;
			}

			// for each value
			for (String v : values) {
				// add subsection
				genR.writeLine(TexUtils.subsection(v));
				genR.writeLine(v.replace("_", "\\textunderscore ")
						+ " is a general runtime.");
				genR.writeLine();

				// gather fitting plots
				ArrayList<PlotConfig> fits = TexUtils.getCustomRuntimePlotFits(
						v, pconfig.getCustomRuntimePlots());

				// add ref line
				if (fits.size() > 0) {
					String refs = TexUtils.getReferenceString(fits, "");
					genR.writeLine(refs);
					genR.writeLine();

					// add plots that contain the value
					for (PlotConfig pc : fits) {
						if (!addedPlots.contains(pc)) {
							addedPlots.add(pc);
						}
					}
				}

				// values
				genR.writeCommentBlock("value table of " + v);

				// check if combined tables
				if (config.isMultipleSeriesTables()) {
					TableMode mode = config.getTableMode();

					// lists of values to be shown in the tables
					ArrayList<Integer> seriesIndexList = new ArrayList<Integer>();
					ArrayList<TableFlag> flagList = new ArrayList<TableFlag>();

					// MODE = ALTERNATING SERIES
					if (mode.equals(TableMode.alternatingSeries)) {
						// fill lists
						for (TableFlag tf : config.getTableFlags()) {
							for (int i = 0; i < batchData.length; i++) {
								seriesIndexList.add(i);
								flagList.add(tf);
							}
						}
					}

					// MODE = ALTERNATING VALUES
					if (mode.equals(TableMode.alternatingValues)) {
						for (int i = 0; i < batchData.length; i++) {
							for (TableFlag tf : config.getTableFlags()) {
								seriesIndexList.add(i);
								flagList.add(tf);

							}
						}
					}

					// generate tables
					TexUtils.generateCombinedTables(seriesNames, batchData,
							genR, v,
							PlotConfig.customPlotDomainGeneralRuntimes,
							seriesIndexList, flagList, max, config);

				} else {
					// else: one table for each flag
					String[] tableDescrArray = TexUtils
							.selectDescription(seriesNames);

					// variables for horizontal alignment
					int currentColumns = 0;
					int currentTables = 0;

					// one table for each flag
					for (TableFlag tf : config.getTableFlags()) {
						// init table
						MultiValueTexTable table = new MultiValueTexTable(genR,
								tableDescrArray, config.getDateFormat(),
								config.getScaling(), config.getMapping(), tf);

						// add values
						for (int i = 0; i < batchData[max].length; i++) {
							long timestamp = batchData[max][i].getTimestamp();

							AggregatedValue[] avs = new AggregatedValue[batchData.length];

							for (int j = 0; j < batchData.length; j++) {
								if (batchData[j].length > i) {
									AggregatedBatch b = batchData[j][i];
									if (b.getGeneralRuntimes().getNames()
											.contains(v))
										avs[j] = b.getGeneralRuntimes().get(v);
									else
										avs[j] = new AggregatedValue(v,
												new double[] { 0.0, 0.0, 0.0,
														0.0, 0.0, 0.0, 0.0,
														0.0, 0.0 });
								}
							}

							table.addDataRow(avs, timestamp);
						}

						// close table
						table.close();

						// check for horizontal alignment
						int t = table.getTableCounter();
						currentColumns += t * tableDescrArray.length;
						currentTables += t;

						// if to large, add blankline and reset
						if ((currentTables + 1) * currentColumns > Config
								.getInt("LATEX_TABLE_MAX_COLUMNS")) {
							genR.writeLine();
							currentColumns = 0;
							currentTables = 0;
						}
					}
				}
			}

			// add plots subsection
			if (addedPlots.size() > 0)
				TexUtils.addPlotsSubsection(genR, "", plotDir, addedPlots);

			genR.writeLine();
		}

		// close and return
		genR.close();
		return genR;
	}

	/** Creates a general runtimes chapter. **/
	public static TexFile generateGeneralRuntimesChapter(String seriesName,
			String dstDir, String plotDir, AggregatedBatch initBatch,
			AggregatedBatch[] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		// write general runtimes
		TexFile genR = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, seriesName + Config.get("LATEX_DELIMITER")
				+ TexUtils.generalRuntimesFilename
				+ Config.get("SUFFIX_TEX_FILE"));

		// added plots
		ArrayList<PlotConfig> addedPlots = new ArrayList<PlotConfig>();

		// if no values, do nothing
		if (initBatch.getGeneralRuntimes().size() > 0) {
			genR.writeCommentBlock(TexUtils.generalRuntimes);
			genR.writeLine(TexUtils.section(TexUtils.generalRuntimes));
			genR.writeLine();

			// for each value
			for (AggregatedValue v : initBatch.getGeneralRuntimes().getList()) {
				// add subsection
				genR.writeLine(TexUtils.subsection(v.getName()));
				genR.writeLine(v.getName().replace("_", "\\textunderscore ")
						+ " is a general runtime.");
				genR.writeLine();

				// gather fitting plots
				ArrayList<PlotConfig> fits = TexUtils.getCustomRuntimePlotFits(
						v.getName(), pconfig.getCustomRuntimePlots());

				// add ref line
				if (fits.size() > 0) {
					String refs = TexUtils.getReferenceString(fits, seriesName);
					genR.writeLine(refs);
					genR.writeLine();

					// add plots that contain the value
					for (PlotConfig pc : fits) {
						if (!addedPlots.contains(pc)) {
							addedPlots.add(pc);
						}
					}
				}

				// values
				genR.writeCommentBlock("value table of " + v.getName());

				// select description
				String[] tableDescrArray = TexUtils.selectDescription(config);

				// init table
				SingleScalarTexTable table = new SingleScalarTexTable(genR,
						tableDescrArray, config.getDateFormat(),
						config.getScaling(), config.getMapping());

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getGeneralRuntimes().getNames()
							.contains(v.getName())) {
						table.addBlankRow(tableDescrArray.length - 1,
								b.getTimestamp());
					} else {
						// select values
						double[] selectedValues = TexUtils
								.formatRuntime(TexUtils.selectValues(b
										.getGeneralRuntimes().get(v.getName()),
										config, false));
						// add row
						table.addRow(selectedValues, b.getTimestamp());
					}
				}

				// close table
				table.close();
				genR.writeLine();
			}

			// add plots subsection
			if (addedPlots.size() > 0)
				TexUtils.addPlotsSubsection(genR, seriesName, plotDir,
						addedPlots);

			genR.writeLine();
		}
		genR.close();
		return genR;
	}

	/** Creates a combined metric runtimes chapter. **/
	public static TexFile generateMetricRuntimesChapter(String[] seriesNames,
			String dstDir, String plotDir, AggregatedBatch[] initBatches,
			AggregatedBatch[][] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		// write statistics
		TexFile metR = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, TexUtils.metricRuntimesFilename
				+ Config.get("SUFFIX_TEX_FILE"));

		// added plots
		ArrayList<PlotConfig> addedPlots = new ArrayList<PlotConfig>();

		// list values
		ArrayList<String> values = new ArrayList<String>();
		for (AggregatedBatch initBatch : initBatches) {
			for (String v : initBatch.getMetricRuntimes().getNames()) {
				if (!values.contains(v))
					values.add(v);
			}
		}

		// check which series has the most batches
		int max = 0;
		for (int i = 1; i < batchData.length; i++) {
			if (batchData[i].length > batchData[max].length)
				max = i;
		}

		// if no values, do nothing
		if (values.size() > 0) {
			metR.writeCommentBlock(TexUtils.metricRuntimes);
			metR.writeLine(TexUtils.section(TexUtils.metricRuntimes));
			metR.writeLine();

			// for each value
			for (String v : values) {
				// add subsection
				metR.writeLine(TexUtils.subsection(v));
				metR.writeLine(v.replace("_", "\\textunderscore ")
						+ " is a metric runtime.");
				metR.writeLine();

				// gather fitting plots
				ArrayList<PlotConfig> fits = TexUtils.getCustomRuntimePlotFits(
						v, pconfig.getCustomRuntimePlots());

				// add ref line
				if (fits.size() > 0) {
					String refs = TexUtils.getReferenceString(fits, "");
					metR.writeLine(refs);
					metR.writeLine();

					// add plots that contain the value
					for (PlotConfig pc : fits) {
						if (!addedPlots.contains(pc)) {
							addedPlots.add(pc);
						}
					}
				}

				// values
				metR.writeCommentBlock("value table of " + v);

				// check if combined tables
				if (config.isMultipleSeriesTables()) {
					TableMode mode = config.getTableMode();

					// lists of values to be shown in the tables
					ArrayList<Integer> seriesIndexList = new ArrayList<Integer>();
					ArrayList<TableFlag> flagList = new ArrayList<TableFlag>();

					// MODE = ALTERNATING SERIES
					if (mode.equals(TableMode.alternatingSeries)) {
						// fill lists
						for (TableFlag tf : config.getTableFlags()) {
							for (int i = 0; i < batchData.length; i++) {
								seriesIndexList.add(i);
								flagList.add(tf);
							}
						}
					}

					// MODE = ALTERNATING VALUES
					if (mode.equals(TableMode.alternatingValues)) {
						for (int i = 0; i < batchData.length; i++) {
							for (TableFlag tf : config.getTableFlags()) {
								seriesIndexList.add(i);
								flagList.add(tf);

							}
						}
					}

					// generate tables
					TexUtils.generateCombinedTables(seriesNames, batchData,
							metR, v, PlotConfig.customPlotDomainMetricRuntimes,
							seriesIndexList, flagList, max, config);

				} else {
					// else: one table for each flag
					String[] tableDescrArray = TexUtils
							.selectDescription(seriesNames);

					// variables for horizontal alignment
					int currentColumns = 0;
					int currentTables = 0;

					// one table for each flag
					for (TableFlag tf : config.getTableFlags()) {
						// init table
						MultiValueTexTable table = new MultiValueTexTable(metR,
								tableDescrArray, config.getDateFormat(),
								config.getScaling(), config.getMapping(), tf);

						// add values
						for (int i = 0; i < batchData[max].length; i++) {
							long timestamp = batchData[max][i].getTimestamp();

							AggregatedValue[] avs = new AggregatedValue[batchData.length];

							for (int j = 0; j < batchData.length; j++) {
								if (batchData[j].length > i) {
									AggregatedBatch b = batchData[j][i];
									if (b.getMetricRuntimes().getNames()
											.contains(v))
										avs[j] = b.getMetricRuntimes().get(v);
									else
										avs[j] = null;
								}
							}

							table.addDataRow(avs, timestamp);
						}

						// close table
						table.close();

						// check for horizontal alignment
						int t = table.getTableCounter();
						currentColumns += t * tableDescrArray.length;
						currentTables += t;

						// if to large, add blankline and reset
						if ((currentTables + 1) * currentColumns > Config
								.getInt("LATEX_TABLE_MAX_COLUMNS")) {
							metR.writeLine();
							currentColumns = 0;
							currentTables = 0;
						}
					}
				}
			}

			// add plots subsection
			if (addedPlots.size() > 0)
				TexUtils.addPlotsSubsection(metR, "", plotDir, addedPlots);

			metR.writeLine();
		}

		// close and return
		metR.close();
		return metR;
	}

	/** Creates a metric runtimes chapter. **/
	public static TexFile generateMetricRuntimesChapter(String seriesName,
			String dstDir, String plotDir, AggregatedBatch initBatch,
			AggregatedBatch[] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		// write metric runtimes
		TexFile metR = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, seriesName + Config.get("LATEX_DELIMITER")
				+ TexUtils.metricRuntimesFilename
				+ Config.get("SUFFIX_TEX_FILE"));

		// added plots
		ArrayList<PlotConfig> addedPlots = new ArrayList<PlotConfig>();

		// if no values, do nothing
		if (initBatch.getMetricRuntimes().size() > 0) {
			metR.writeCommentBlock(TexUtils.metricRuntimes);
			metR.writeLine(TexUtils.section(TexUtils.metricRuntimes));
			metR.writeLine();

			// for each value
			for (AggregatedValue v : initBatch.getMetricRuntimes().getList()) {
				metR.writeLine(TexUtils.subsection(v.getName()));
				metR.writeLine(v.getName().replace("_", "\\textunderscore ")
						+ " is a metric runtime.");
				metR.writeLine();

				// gather fitting plots
				ArrayList<PlotConfig> fits = TexUtils.getCustomRuntimePlotFits(
						v.getName(), pconfig.getCustomRuntimePlots());

				// add ref line
				if (fits.size() > 0) {
					String refs = TexUtils.getReferenceString(fits, seriesName);
					metR.writeLine(refs);
					metR.writeLine();

					// add plots that contain the value
					for (PlotConfig pc : fits) {
						if (!addedPlots.contains(pc)) {
							addedPlots.add(pc);
						}
					}
				}

				// values
				metR.writeCommentBlock("value table of " + v.getName());

				// select description
				String[] tableDescrArray = TexUtils.selectDescription(config);

				// init table
				SingleScalarTexTable table = new SingleScalarTexTable(metR,
						tableDescrArray, config.getDateFormat(),
						config.getScaling(), config.getMapping());

				// add values
				for (AggregatedBatch b : batchData) {
					if (!b.getMetricRuntimes().getNames().contains(v.getName())) {
						table.addBlankRow(tableDescrArray.length - 1,
								b.getTimestamp());
					} else {
						// select values
						double[] selectedValues = TexUtils
								.formatRuntime(TexUtils.selectValues(b
										.getMetricRuntimes().get(v.getName()),
										config, false));

						// add row
						table.addRow(selectedValues, b.getTimestamp());
					}
				}

				// close table
				table.close();
				metR.writeLine();

			}

			// add plots subsection
			if (addedPlots.size() > 0)
				TexUtils.addPlotsSubsection(metR, seriesName, plotDir,
						addedPlots);

			metR.writeLine();
		}
		metR.close();
		return metR;
	}

	public static String getReferenceString(ArrayList<PlotConfig> fits,
			String seriesName) {
		String refs;
		if (fits.size() > 1)
			refs = "See plots: ";
		else
			refs = "See plot: ";
		boolean first = true;
		for (PlotConfig pc : fits) {
			if (pc.getPlotAsCdf().equals("true")) {
				if (first) {
					refs += TexUtils.ref(TexUtils.getPlotLabel(seriesName,
							pc.getFilename() + TexUtils.cdfSuffix));
					first = false;
				} else {
					refs += ", "
							+ TexUtils.ref(TexUtils.getPlotLabel(seriesName,
									pc.getFilename() + TexUtils.cdfSuffix));
				}
			} else if (pc.getPlotAsCdf().equals("both")) {
				if (first) {
					refs += TexUtils.ref(TexUtils.getPlotLabel(seriesName,
							pc.getFilename()))
							+ ", "
							+ TexUtils.ref(TexUtils.getPlotLabel(seriesName,
									pc.getFilename() + TexUtils.cdfSuffix));
					first = false;
				} else {
					refs += ", "
							+ TexUtils.ref(TexUtils.getPlotLabel(seriesName,
									pc.getFilename()))
							+ ", "
							+ TexUtils.ref(TexUtils.getPlotLabel(seriesName,
									pc.getFilename() + TexUtils.cdfSuffix));
				}
			} else {
				if (first) {
					refs += TexUtils.ref(TexUtils.getPlotLabel(seriesName,
							pc.getFilename()));
					;
					first = false;
				} else {
					refs += ", "
							+ TexUtils.ref(TexUtils.getPlotLabel(seriesName,
									pc.getFilename()));
					;
				}
			}
		}
		refs += ".";
		return refs;
	}

	/** Generates a combined metric chapter. **/
	public static TexFile generateMetricChapter(String dstDir, String plotDir,
			SeriesData[] series, AggregatedMetric m,
			AggregatedBatch[][] batchData, TexConfig config,
			PlottingConfig pconfig) throws IOException {
		TexFile mFile = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, m.getName() + Config.get("SUFFIX_TEX_FILE"));
		mFile.writeCommentBlock(m.getName());
		mFile.writeMetric(series, m, batchData, plotDir, config, pconfig);
		mFile.close();
		return mFile;
	}

	/** Generates a chapter for the given metric and series. **/
	public static TexFile generateMetricChapter(String dstDir, String plotDir,
			SeriesData s, AggregatedMetric m, AggregatedBatch[] batchData,
			TexConfig config, PlottingConfig pconfig) throws IOException {
		TexFile mFile = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, s.getName() + Config.get("LATEX_DELIMITER")
				+ m.getName() + Config.get("SUFFIX_TEX_FILE"));
		mFile.writeCommentBlock(m.getName());
		mFile.writeMetric(s, m, batchData, plotDir, config, pconfig);
		mFile.close();
		return mFile;
	}

	/** Adds the Plots-Section containing all added plots in the ArrayList. **/
	public static void addPlotsSubsection(TexFile file, String seriesName,
			String plotDir, ArrayList<PlotConfig> addedPlots)
			throws IOException {
		// add plots subsection
		file.writeLine(TexUtils.subsection(TexUtils.plots));
		file.writeLine();

		// add plots
		for (PlotConfig pc : addedPlots) {
			if (pc.getPlotAsCdf().equals("true")) {
				file.includeFigure(plotDir, pc.getFilename()
						+ TexUtils.cdfSuffix, seriesName);
			} else if (pc.getPlotAsCdf().equals("both")) {
				file.includeFigure(plotDir, pc.getFilename(), seriesName);
				file.includeFigure(plotDir, pc.getFilename()
						+ TexUtils.cdfSuffix, seriesName);
			} else {
				file.includeFigure(plotDir, pc.getFilename(), seriesName);
			}
		}
	}

	/** Returns a table description with the given series names. **/
	public static String[] selectDescription(String[] seriesNames) {
		String[] tableDescr = new String[seriesNames.length + 1];
		tableDescr[0] = "Timestamp";
		for (int i = 0; i < seriesNames.length; i++)
			tableDescr[i + 1] = seriesNames[i]
					.replace("_", "\\textunderscore ");
		return tableDescr;
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

	/** Returns the custom runtime plots that contain the given runtime. **/
	public static ArrayList<PlotConfig> getCustomRuntimePlotFits(
			String runtime, ArrayList<PlotConfig> plots) {
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
				if (!(dom.equals(PlotConfig.customPlotDomainRuntimes)
						|| dom.equals(PlotConfig.customPlotDomainGeneralRuntimes) || dom
							.equals(PlotConfig.customPlotDomainMetricRuntimes)))
					continue;

				// if contains value, add plot to
				if (val.equals(runtime)
						|| val.equals(PlotConfig.customPlotWildcard)) {
					if (!fits.contains(p)) {
						fits.add(p);
						finished = true;
					}
				}
			}
		}
		return fits;
	}

	/**
	 * Returns the custom metric value plots that contain the given metric
	 * value.
	 **/
	public static ArrayList<PlotConfig> getCustomMetricValuePlotFits(
			String metric, String value, ArrayList<PlotConfig> plots) {
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
				if (!(dom.equals(metric)))
					continue;

				// if contains value, add plot to
				if (val.equals(value)
						|| val.equals(PlotConfig.customPlotWildcard)) {
					if (!fits.contains(p)) {
						fits.add(p);
						finished = true;
					}
				}
			}
		}
		return fits;
	}

	/** Adds the default metric value plots that contain the given metric value. **/
	public static void addDefaultMetricValuePlotFits(
			ArrayList<PlotConfig> fits, String metric, String value) {
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")
				&& Config.getBoolean("DEFAULT_PLOT_METRIC_VALUES")) {
			fits.add(PlotConfig.generateDummyPlotConfig(metric + "." + value,
					"false", null, null, null));
		}
	}

	/**
	 * Returns the custom distribution plots that contain the given
	 * distribution.
	 **/
	public static ArrayList<PlotConfig> getCustomDistributionPlotFits(
			String metric, String dist, ArrayList<PlotConfig> plots) {
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
				if (!(dom.equals(metric)))
					continue;

				// if contains value, add plot to
				if (val.equals(dist)
						|| val.equals(PlotConfig.customPlotWildcard)) {
					if (!fits.contains(p)) {
						fits.add(p);
						finished = true;
					}
				}
			}
		}
		return fits;
	}

	/** Adds the default distribution plots that contain the given distribution. **/
	public static void addDefaultDistributionPlotFits(
			ArrayList<PlotConfig> fits, String metric, String dist,
			DistributionPlotType distPlotType) {
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")
				&& Config.getBoolean("DEFAULT_PLOT_DISTRIBUTIONS")) {
			switch (distPlotType) {
			case cdfOnly:
				fits.add(PlotConfig.generateDummyPlotConfig(
						metric + "." + dist, "true", null, null, null));
				break;
			case distOnly:
				fits.add(PlotConfig.generateDummyPlotConfig(
						metric + "." + dist, "false", null, null, null));
				break;
			case distANDcdf:
				fits.add(PlotConfig.generateDummyPlotConfig(
						metric + "." + dist, "both", null, null, null));
				break;
			}
		}
	}

	/**
	 * Returns the custom nodevaluelist plots that contain the given
	 * nodevaluelist.
	 **/
	public static ArrayList<PlotConfig> getCustomNodeValueListPlotFits(
			String metric, String nvl, ArrayList<PlotConfig> plots) {
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
				if (!(dom.equals(metric)))
					continue;

				// if contains value, add plot to
				if (val.equals(nvl)
						|| val.equals(PlotConfig.customPlotWildcard)) {
					if (!fits.contains(p)) {
						fits.add(p);
						finished = true;
					}
				}
			}
		}
		return fits;
	}

	/**
	 * Adds the default nodevaluelist plots that contain the given
	 * nodevaluelist.
	 **/
	public static void addDefaultNodeValueListPlotFits(
			ArrayList<PlotConfig> fits, String metric, String nvl) {
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")
				&& Config.getBoolean("DEFAULT_PLOT_NODEVALUELISTS")) {
			fits.add(PlotConfig.generateDummyPlotConfig(metric + "." + nvl,
					"false", null, null, null));
		}
	}

	/** Creates the default header file. **/
	public static TexFile generateHeaderFile(String dstDir) throws IOException {
		TexFile header = new TexFile(dstDir + Config.get("LATEX_CHAPTERS_DIR")
				+ Dir.delimiter, TexUtils.headerFilename
				+ Config.get("SUFFIX_TEX_FILE"));

		// auto gen
		header.writeCommentBlock(TexUtils.headerFilename
				+ Config.get("SUFFIX_TEX_FILE"));
		header.writeCommentBlock("This is an auto-generated tex-file from DNA - dynammic network analyzer.");
		header.writeLine();

		header.writeLine(TexUtils.cmd("documentclass") + "[%");
		header.writeLine("\tpagesize=pdftex,%");
		header.writeLine("\tpaper=a4,%");
		header.writeLine("\twoside=false,%");
		header.writeLine("\tparskip=half,%");
		header.writeLine("\tchapterprefix,%");
		header.writeLine("\tfontsize=12pt,%");
		header.writeLine("\tenglish,");
		header.writeLine("]" + TexUtils.argument("scrreprt"));

		header.writeLine();

		header.writeLine(TexUtils.usepackage("geometry",
				"margin=2.5cm, left=3cm"));
		header.writeLine(TexUtils.usepackage("grffile"));
		header.writeLine(TexUtils.usepackage("scrpage2"));

		header.writeLine();
		header.writeCommentLine("Package for utf8 charsets");
		header.writeLine(TexUtils.usepackage("inputenc", "utf8"));

		header.writeLine();
		header.writeCommentLine("Package for index-generation");
		header.writeLine(TexUtils.usepackage("makeidx"));

		header.writeLine();
		header.writeLine(TexUtils.usepackage("acronym",
				"nohyperlinks,printonlyused"));

		header.writeLine();
		header.writeCommentLine("Package for translation in german");
		header.writeLine(TexUtils.usepackage("babel", "english"));

		header.writeLine();
		header.writeCommentLine("graphic packages");
		header.writeLine(TexUtils.usepackage("graphicx", "pdftex"));
		header.writeLine(TexUtils.usepackage("subfigure"));
		header.writeLine(TexUtils.usepackage("caption",
				"margin=10pt,font=small,labelfont=bf,labelsep=endash"));

		header.writeLine();
		header.writeCommentLine("Use special font");
		header.writeLine(TexUtils.usepackage("microtype"));

		header.writeLine();
		header.writeCommentLine("Special font in koma script");
		header.writeLine(TexUtils.cmd("setkomafont", "sectioning")
				+ TexUtils.argument(TexUtils.cmd("normalfont")
						+ TexUtils.cmd("bfseries")));
		header.writeLine(TexUtils.cmd("setkomafont", "descriptionlabel")
				+ TexUtils.argument(TexUtils.cmd("normalfont")
						+ TexUtils.cmd("bfseries")));

		header.writeLine();
		header.writeCommentLine("Linebreak in image descriptions");
		header.writeLine(TexUtils.cmd("setcapindent", "1em"));

		header.writeLine();
		header.writeCommentLine("Header and footer");
		header.writeLine(TexUtils.pagestyle("useheadings"));

		header.writeLine();
		header.writeLine(TexUtils.cmd("renewcommand", TexUtils.cmd("rmdefault"))
				+ TexUtils.argument("bch"));
		header.writeLine(TexUtils.cmd("renewcommand", TexUtils.cmd("sfdefault"))
				+ TexUtils.argument("phy"));
		header.writeLine(TexUtils.cmd("renewcommand", TexUtils.cmd("ttdefault"))
				+ TexUtils.argument("txtt"));

		header.writeLine();
		header.writeCommentLine("Package for colours in pdf");
		header.writeLine(TexUtils.usepackage("color"));

		header.writeLine();
		header.writeCommentLine("Package for links in pdf");
		header.writeLine(TexUtils.definecolor("LinkColor", "rgb", "0,0,0.5"));
		header.writeLine(TexUtils.cmd("usepackage") + "[%");
		header.writeLine("\tpdftitle="
				+ TexUtils.argument(TexUtils.cmd("Project") + " "
						+ TexUtils.cmd("DocumentType")) + ",%");
		header.writeLine("\tpdfauthor="
				+ TexUtils.argument(TexUtils.cmd("AuthorName")) + ",");
		header.writeLine("\tbookmarksopen=true,");
		header.writeLine("\tbookmarksopenlevel=1");
		header.writeLine("\t]" + TexUtils.argument("hyperref"));
		header.writeLine(TexUtils.cmd("hypersetup") + "{colorlinks=true,%");
		header.writeLine("\tlinkcolor=" + TexUtils.argument("black") + ",%");
		header.writeLine("\tcitecolor=" + TexUtils.argument("black") + ",%");
		header.writeLine("\tfilecolor=" + TexUtils.argument("black") + ",%");
		header.writeLine("\tmenucolor=" + TexUtils.argument("black") + ",%");
		header.writeLine("\tpagecolor=" + TexUtils.argument("black") + ",%");
		header.writeLine("\turlcolor=" + TexUtils.argument("black") + "}");
		header.writeLine(TexUtils.usepackage("hypcap", "all"));
		header.writeLine(TexUtils.usepackage("hypbmsec"));

		header.writeLine();
		header.writeCommentLine("Listing formatting");
		header.writeLine(TexUtils.usepackage("listings", "savemem"));
		header.writeLine(TexUtils.cmd("lstloadlanguages", "TeX"));
		header.writeLine(TexUtils.usepackage("verbatim"));

		header.writeCommentBlock("-----------------------------------------------");
		header.writeLine(TexUtils.cmd("lstset") + "{language=C++,");
		header.writeLine("\tbasicstyle=" + TexUtils.cmd("ttfamily")
				+ TexUtils.cmd("small") + ",");
		header.writeLine("\tbasewidth=.52cm,");
		header.writeLine("\ttabsize=2,");
		header.writeLine("\txleftmargin=" + TexUtils.cmd("leftmargin") + ",");
		header.writeLine("\taboveskip=" + TexUtils.cmd("bigskipamount") + ",");
		header.writeLine("\tbelowskip=" + TexUtils.cmd("smallskipamount") + ",");
		header.writeLine("\tfloat=h,");
		header.writeLine("\tcaptionpos=b,");
		header.writeLine("\tabovecaptionskip=" + TexUtils.cmd("medskipamount")
				+ ",");
		header.writeLine("}");

		header.writeLine();
		header.writeCommentBlock("-----------------------------------------------");
		header.writeLine(TexUtils.usepackage("float"));
		header.writeLine(TexUtils.cmd("newfloat")
				+ TexUtils.argument("sourcecode") + TexUtils.argument("tbp")
				+ TexUtils.argument("loc") + TexUtils.option("chapter"));
		header.writeLine(TexUtils.cmd("floatname")
				+ TexUtils.argument("sourcecode")
				+ TexUtils.argument("Quelltext"));

		header.writeLine();
		header.writeCommentBlock("-----------------------------------------------");
		header.writeLine(TexUtils.cmd("newenvironment", "ListChanges") + "%");
		header.writeLine("\t"
				+ TexUtils.argument(TexUtils.begin("list")
						+ TexUtils.argument("$"
								+ TexUtils.cmd("diamondsuit" + "$")
								+ TexUtils.argument(""))) + "%");
		header.writeLine("\t" + TexUtils.argument(TexUtils.end("list")));
		header.writeLine(TexUtils.cmd("newenvironment", "mv")
				+ TexUtils.argument(TexUtils.cmd("addmargin")
						+ TexUtils.option(TexUtils.cmd("leftmargin"))
						+ TexUtils.argument("0em") + TexUtils.cmd("verbatim"))
				+ TexUtils.argument(TexUtils.cmd("endverbatim")
						+ TexUtils.cmd("endaddmargin")));
		header.writeLine(TexUtils.cmd("newenvironment", "ml")
				+ TexUtils.option("1"));
		header.writeLine("\t"
				+ TexUtils.argument(TexUtils.cmd("addmargin")
						+ TexUtils.option(TexUtils.cmd("leftmargin"))
						+ TexUtils.argument("0em")
						+ TexUtils.cmd("labeling")
						+ TexUtils.option(TexUtils.cmd("hspace")
								+ TexUtils.cmd("labelsep") + "--")
						+ TexUtils.argument("#1") + TexUtils.cmd("setlength")
						+ TexUtils.cmd("itemsep", "-.1em")));
		header.writeLine("\t"
				+ TexUtils.argument(TexUtils.cmd("endlabeling")
						+ TexUtils.cmd("endaddmargin")));

		header.writeLine();
		header.writeCommentLine("Create index");
		header.writeLine(TexUtils.cmd("makeindex"));

		// close
		header.close();
		return header;
	}

	/** Copies the logo the titlepage-logo. **/
	public static void copyLogo(String dstDir) throws IOException {
		InputStream is = null;
		JarFile x = null;
		if (Config.isRunFromJar()) {
			Path pPath;

			try {
				pPath = Paths.get(Config.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI());
				x = new JarFile(pPath.toFile(), false);
				is = x.getInputStream(x.getEntry(Config
						.get("LATEX_LOGO_FILENAME")
						+ Config.get("LATEX_LOGO_SUFFIX")));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			is = new FileInputStream(Config.get("LATEX_LOGO_SRC_DIR")
					+ Config.get("LATEX_LOGO_FILENAME")
					+ Config.get("LATEX_LOGO_SUFFIX"));
		}

		File dst = new File(dstDir + Config.get("LATEX_IMAGES_DIR")
				+ Dir.delimiter);
		dst.mkdirs();
		FileOutputStream os = new FileOutputStream(new File(dstDir
				+ Config.get("LATEX_IMAGES_DIR") + Dir.delimiter
				+ TexUtils.logoFilename + TexUtils.logoSuffix));

		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = is.read(bytes)) != -1) {
			os.write(bytes, 0, read);
		}
		if (is != null)
			is.close();
		if (os != null)
			os.close();
		if (x != null)
			x.close();
	}

	/** Returns an abbreviation of the flag as string. **/
	public static String getShortString(TableFlag flag) {
		switch (flag) {
		case Average:
			return "Avg";
		case Min:
			return "Min";
		case Max:
			return "Max";
		case Median:
			return "Med";
		case Var:
			return "Var";
		case VarLow:
			return "Var-";
		case VarUp:
			return "Var+";
		case ConfLow:
			return "Conf-";
		case ConfUp:
			return "Conf+";
		case all:
			return "All";
		default:
			return null;
		}
	}

	/**
	 * Formats the contained double values to runtimes in seconds or
	 * milliseconds, depending on what is set in the config.
	 **/
	public static AggregatedValue formatRuntime(AggregatedValue runtime) {
		double x = 1;
		if (Config.get("LATEX_RUNTIME_FORMAT").equals("seconds"))
			x = 1000 * 1000 * 1000;
		if (Config.get("LATEX_RUNTIME_FORMAT").equals("milliseconds"))
			x = 1000 * 1000;

		double[] tempValues = Arrays.copyOf(runtime.getValues(),
				runtime.getValues().length);

		for (int i = 0; i < tempValues.length; i++) {
			tempValues[i] = tempValues[i] / x;
		}

		return new AggregatedValue(runtime.getName(), tempValues);
	}

	/**
	 * Formats the contained double value to runtime in seconds or milliseconds,
	 * depending on what is set in the config.
	 **/
	public static double[] formatRuntime(double[] runtimeValues) {
		double x = 1;
		if (Config.get("LATEX_RUNTIME_FORMAT").equals("seconds"))
			x = 1000 * 1000 * 1000;
		if (Config.get("LATEX_RUNTIME_FORMAT").equals("milliseconds"))
			x = 1000 * 1000;

		double[] tempValues = Arrays
				.copyOf(runtimeValues, runtimeValues.length);

		for (int i = 0; i < tempValues.length; i++) {
			tempValues[i] = tempValues[i] / x;
		}

		return tempValues;
	}

}
