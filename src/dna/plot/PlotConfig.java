package dna.plot;

import java.util.ArrayList;

import dna.plot.PlottingConfig.ValueSortMode;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.util.Config;
import dna.util.Log;

/**
 * Configuration object representing one plot which will be read from properties
 * config file.
 * 
 * @author RWilmes
 */
public class PlotConfig {

	// custom plot configuration
	// PREFIXES
	public static String customPlotPrefixStatistics = "ST_";

	public static String customPlotPrefixRuntimes = "RT_";

	public static String customPlotPrefixMetricvalues = "MV_";

	public static String customPlotPrefixValues = "CUSTOM_";

	public static String customPlotPrefixDistributions = "MD_";

	public static String customPlotPrefixNodeValueLists = "MNVL_";

	// SUFFIXES
	public static String customPlotSuffixValues = "_VALUES";

	public static String customPlotSuffixCdf = "_CDF";

	public static String customPlotSuffixFilename = "_FILENAME";

	public static String customPlotSuffixTitle = "_TITLE";

	public static String customPlotSuffixDatetime = "_DATETIME";

	public static String customPlotSuffixTimeDataFormat = "_TIMEDATAFORMAT";

	public static String customPlotSuffixLogscale = "_LOGSCALE";

	public static String customPlotSuffixXLabel = "_XLABEL";

	public static String customPlotSuffixYLabel = "_YLABEL";

	public static String customPlotSuffixXOffset = "_XOFFSET";

	public static String customPlotSuffixYOffset = "_YOFFSET";

	public static String customPlotSuffixXRange = "_XRANGE";

	public static String customPlotSuffixYRange = "_YRANGE";

	public static String customPlotSuffixXTics = "_XTICS";

	public static String customPlotSuffixYTics = "_YTICS";

	public static String customPlotSuffixXScaling = "_XSCALING";

	public static String customPlotSuffixYScaling = "_YSCALING";

	public static String customPlotSuffixDistType = "_TYPE";

	public static String customPlotSuffixNvlOrder = "_ORDER";

	public static String customPlotSuffixNvlOrderBy = "_ORDERBY";

	public static String customPlotSuffixPlotStyle = "_STYLE";

	public static String customPlotValueSortMode = "_SORTMODE";

	public static String customPlotValueSortList = "_SORTLIST";

	public static String customPlotSuffixKey = "_KEY";

	public static String customPlotSuffixDomain = "_DOMAIN";

	// DOMAINS
	public static String customPlotDomainStatistics = "statistics";

	public static String customPlotDomainRuntimes = "runtimes";

	public static String customPlotDomainMetricRuntimes = "metric_runtimes";

	public static String customPlotDomainGeneralRuntimes = "general_runtimes";

	public static String customPlotDomainFunction = "function";

	public static String customPlotDomainExpression = "expression";

	// CHARACTERS
	public static String customPlotWildcard = "*";
	public static String customPlotDomainDelimiter = "~";

	// DUMMY VARIABLE
	public static String dummyVariable = "DUMMYVARIABLEREPLACEMENT";

	// DEFAULT CONFIG KEYS
	public static String gnuplotDefaultKeyKey = "GNUPLOT_KEY";
	public static String gnuplotDefaultKeyCdfKey = "GNUPLOT_KEY_CDF";

	public static String gnuplotDefaultKeyDateTime = "GNUPLOT_DATETIME";
	public static String gnuplotDefaultKeyPlotDateTime = "GNUPLOT_PLOTDATETIME";
	public static String gnuplotDefaultKeyTimeDataFormat = "GNUPLOT_TIMEDATAFORMAT";

	public static String gnuplotDefaultKeyXLabel = "GNUPLOT_XLABEL";
	public static String gnuplotDefaultKeyYLabel = "GNUPLOT_YLABEL";

	public static String gnuplotDefaultKeyXOffset = "GNUPLOT_XOFFSET";
	public static String gnuplotDefaultKeyYOffset = "GNUPLOT_YOFFSET";

	public static String gnuplotDefaultKeyXRange = "GNUPLOT_XRANGE";
	public static String gnuplotDefaultKeyYRange = "GNUPLOT_YRANGE";

	public static String gnuplotDefaultXScaling = "GNUPLOT_XSCALING";
	public static String gnuplotDefaultYScaling = "GNUPLOT_YSCALING";

	public static String gnuplotDefaultKeyPlotType = "GNUPLOT_DEFAULT_PLOTTYPE";
	public static String gnuplotDefaultKeyPlotStyle = "GNUPLOT_DEFAULT_PLOTSTYLE";

	public static String gnuplotDefaultKeyDistPlotType = "GNUPLOT_DEFAULT_DIST_PLOTTYPE";

	public static String gnuplotDefaultKeyNodeValueListOrder = "GNUPLOT_DEFAULT_NVL_ORDER";
	public static String gnuplotDefaultKeyNodeValueListOrderBy = "GNUPLOT_DEFAULT_NVL_ORDERBY";

	public static String gnuplotDefaultKeyValueSortMode = "GNUPLOT_DEFAULT_VALUE_SORT_MODE";
	public static String gnuplotDefaultKeyValueSortList = "GNUPLOT_DEFAULT_VALUE_SORT_LIST";

	public static double[] gnuplotZeroLineNoIndex = new double[] { 0, 0, 0, 0,
			0, 0, 0, 0, 0 };
	public static double[] gnuplotZeroLineWithIndex = new double[] { 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0 };

	// variables
	private String filename;
	private String title;
	private String key;
	private String xLabel;
	private String yLabel;
	private String logscale;
	private String datetime;
	private String timeDataFormat;
	private double xOffset;
	private double yOffset;
	private String xRange;
	private String yRange;
	private String xTics;
	private String yTics;
	private String xScaling;
	private String yScaling;
	private String plotAsCdf;
	private String[] values;
	private String[] domains;
	private PlotStyle style;
	private ValueSortMode valueSortMode;
	private String[] valueSortList;
	private DistributionPlotType distPlotType;
	private NodeValueListOrder order;
	private NodeValueListOrderBy orderBy;
	private boolean plotAll;
	private String generalDomain;

	// constructor
	private PlotConfig(String filename, String title, String key,
			String xLabel, String yLabel, String logscale, String datetime,
			String timeDataFormat, double xOffset, double yOffset,
			String xRange, String yRange, String xTics, String yTics,
			String xScaling, String yScaling, String plotAsCdf,
			String[] values, String[] domains, PlotStyle style,
			ValueSortMode valueSortMode, String[] valueSortList,
			DistributionPlotType distPlotType, NodeValueListOrder order,
			NodeValueListOrderBy orderBy, boolean plotAll, String generalDomain) {
		this.filename = filename;
		this.title = title;
		this.key = key;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.logscale = logscale;
		this.datetime = datetime;
		this.timeDataFormat = timeDataFormat;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xRange = xRange;
		this.yRange = yRange;
		this.xTics = xTics;
		this.yTics = yTics;
		this.xScaling = xScaling;
		this.yScaling = yScaling;
		this.plotAsCdf = plotAsCdf;
		this.values = values;
		this.domains = domains;
		this.style = style;
		this.valueSortMode = valueSortMode;
		this.valueSortList = valueSortList;
		this.distPlotType = distPlotType;
		this.order = order;
		this.orderBy = orderBy;
		this.plotAll = plotAll;
		this.generalDomain = generalDomain;
	}

	// getters
	public String[] getValues() {
		return values;
	}

	public String[] getDomains() {
		return domains;
	}

	public String getPlotAsCdf() {
		return plotAsCdf;
	}

	public PlotStyle getStyle() {
		return style;
	}

	public ValueSortMode getValueSortMode() {
		return valueSortMode;
	}

	public String[] getValueSortList() {
		return valueSortList;
	}

	public DistributionPlotType getDistPlotType() {
		return distPlotType;
	}

	public NodeValueListOrder getOrder() {
		return order;
	}

	public NodeValueListOrderBy getOrderBy() {
		return orderBy;
	}

	public boolean isPlotAll() {
		return plotAll;
	}

	public String getFilename() {
		return filename;
	}

	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}

	public String getxLabel() {
		return xLabel;
	}

	public String getyLabel() {
		return yLabel;
	}

	public String getLogscale() {
		return logscale;
	}

	public String getDatetime() {
		return datetime;
	}

	public String getTimeDataFormat() {
		return timeDataFormat;
	}

	public double getxOffset() {
		return xOffset;
	}

	public double getyOffset() {
		return yOffset;
	}

	public String getxRange() {
		return xRange;
	}

	public String getyRange() {
		return yRange;
	}

	public String getxTics() {
		return xTics;
	}

	public String getyTics() {
		return yTics;
	}

	public String getxScaling() {
		return xScaling;
	}

	public String getyScaling() {
		return yScaling;
	}

	public String getGeneralDomain() {
		return generalDomain;
	}

	// setters
	public void setValues(String[] values) {
		this.values = values;
	}

	public void setDomains(String[] domains) {
		this.domains = domains;
	}

	/** Returns the custom value plots created from config **/
	public static ArrayList<PlotConfig> getCustomValuePlots() {
		return PlotConfig.getCustomPlots(PlotConfig.customPlotPrefixValues);
	}

	/** Returns the custom statistic plots created from config **/
	public static ArrayList<PlotConfig> getCustomStatisticPlots() {
		return PlotConfig.getCustomPlots(PlotConfig.customPlotPrefixStatistics);
	}

	/** Returns the custom runtime plots created from config **/
	public static ArrayList<PlotConfig> getCustomRuntimePlots() {
		return PlotConfig.getCustomPlots(PlotConfig.customPlotPrefixRuntimes);
	}

	/** Returns the custom metric values plots created from config **/
	public static ArrayList<PlotConfig> getCustomMetricValuePlots() {
		return PlotConfig
				.getCustomPlots(PlotConfig.customPlotPrefixMetricvalues);
	}

	/** Returns the custom metric distribution plots created from config **/
	public static ArrayList<PlotConfig> getCustomMetricDistributionPlots() {
		return PlotConfig
				.getCustomPlots(PlotConfig.customPlotPrefixDistributions);
	}

	/** Returns the custom metric nodevaluelist plots created from config **/
	public static ArrayList<PlotConfig> getCustomMetricNodeValueListPlots() {
		return PlotConfig
				.getCustomPlots(PlotConfig.customPlotPrefixNodeValueLists);
	}

	/** Returns the custom plots created from config **/
	private static ArrayList<PlotConfig> getCustomPlots(String prefix) {
		// define suffix
		String[] plots = Config.keys(prefix + "PLOTS");

		// init list of configs
		ArrayList<PlotConfig> plotConfigs = new ArrayList<PlotConfig>(
				plots.length);

		for (String s : plots) {
			// get plot from config
			String keyword = s;
			String generalDomain = null;
			boolean plotAll = false;

			// set general domain
			if (prefix.equals(PlotConfig.customPlotPrefixStatistics))
				generalDomain = PlotConfig.customPlotDomainStatistics;
			if (prefix.equals(PlotConfig.customPlotPrefixRuntimes))
				generalDomain = PlotConfig.customPlotDomainRuntimes;

			// get values
			String[] values = Config.keys(prefix + s
					+ PlotConfig.customPlotSuffixValues);

			// if no values are set, continue with next plot
			if (values.length < 1)
				continue;

			// init domains arrays
			String[] domains = new String[values.length];

			// replace "-characters in values
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].replace("" + '"', "");
			}

			// parse values and get domains
			for (int i = 0; i < values.length; i++) {
				String value = values[i];

				// check if function
				String[] split = value.split("=");
				if (split.length == 2) {
					// if function -> set domain and continue with next value
					domains[i] = PlotConfig.customPlotDomainFunction;
					continue;
				}

				// check if expression
				split = value.split(":");
				if (split.length == 2) {
					// minor syntax check on amount of $
					int count = 0;
					for (int j = 0; j < split[1].length(); j++) {
						if (split[1].charAt(j) == '$')
							count++;
					}
					if ((count & 1) != 0)
						Log.warn("syntax error on parsing '" + value + "'");

					// check if contains wildcard
					int wildcardCounter = 0;
					String[] delSplit = split[1].split("\\$");
					for (int j = 1; j < delSplit.length; j += 2) {
						if (delSplit[j].contains(PlotConfig.customPlotWildcard)) {
							if (delSplit[j].length() > 1) {
								String[] pSplit = delSplit[j]
										.split(PlotConfig.customPlotDomainDelimiter);
								if (pSplit.length == 2)
									generalDomain = pSplit[0];
							}
							plotAll = true;
							wildcardCounter++;
						}
					}
					if (wildcardCounter > 1)
						Log.warn("More than 1 wildcard in '" + value + "'");

					// if expression -> set domain and continue with next value
					domains[i] = PlotConfig.customPlotDomainExpression;
					continue;
				}

				// check if contains domain
				split = value.split(PlotConfig.customPlotDomainDelimiter);

				if (split.length > 1) {
					String domain = "";
					for (int j = 0; j < split.length - 1; j++) {
						if (j == 0)
							domain += split[j];
						else
							domain += PlotConfig.customPlotDomainDelimiter
									+ split[j];
					}
					// take first part as domain
					domains[i] = domain;

					// take last part as value name
					values[i] = split[split.length - 1];

					// check for wildcard
					if (values[i].equals(PlotConfig.customPlotWildcard)
							|| domains[i].equals(PlotConfig.customPlotWildcard))
						plotAll = true;

					// continue with next value
					continue;
				}

				// if value doesnt contain domain
				if (prefix.equals(PlotConfig.customPlotPrefixRuntimes))
					domains[i] = PlotConfig.customPlotDomainRuntimes;

				//
				if (prefix.equals(PlotConfig.customPlotPrefixStatistics))
					domains[i] = PlotConfig.customPlotDomainStatistics;

				if (value.equals(PlotConfig.customPlotWildcard))
					plotAll = true;

				if (Config.get(prefix + s + PlotConfig.customPlotSuffixDomain) != null)
					domains[i] = Config.get(prefix + s
							+ PlotConfig.customPlotSuffixDomain);

				if (domains[i] == null)
					Log.warn("custom plot config parsing failure: '" + value
							+ "' has unknown domain!");
			}

			// read optional values from config
			String plotAsCdf = "false";

			// default values
			String filename = keyword;
			String xLabel = Config.get(PlotConfig.gnuplotDefaultKeyXLabel);
			String yLabel = Config.get(PlotConfig.gnuplotDefaultKeyYLabel);
			String logscale = null;
			String datetime = null;
			String timeDataFormat = Config
					.get(PlotConfig.gnuplotDefaultKeyTimeDataFormat);
			double xOffset = Config
					.getDouble(PlotConfig.gnuplotDefaultKeyXOffset);
			double yOffset = Config
					.getDouble(PlotConfig.gnuplotDefaultKeyYOffset);
			String xRange = Config.get(PlotConfig.gnuplotDefaultKeyXRange);
			String yRange = Config.get(PlotConfig.gnuplotDefaultKeyYRange);
			String xTics = null;
			String yTics = null;
			String xScaling = Config.get(PlotConfig.gnuplotDefaultXScaling);
			String yScaling = Config.get(PlotConfig.gnuplotDefaultYScaling);
			PlotStyle style = null;
			ValueSortMode valueSortMode = Config
					.getValueSortMode(PlotConfig.gnuplotDefaultKeyValueSortMode);
			String[] valueSortList = Config
					.keys(PlotConfig.gnuplotDefaultKeyValueSortList);
			DistributionPlotType distPlotType = Config
					.getDistributionPlotType(PlotConfig.gnuplotDefaultKeyDistPlotType);
			NodeValueListOrder order = Config
					.getNodeValueListOrder(PlotConfig.gnuplotDefaultKeyNodeValueListOrder);
			NodeValueListOrderBy orderBy = Config
					.getNodeValueListOrderBy(PlotConfig.gnuplotDefaultKeyNodeValueListOrderBy);

			// read config values
			// filename
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixFilename) != null)
				filename = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixFilename);

			// title
			String title = filename;
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixTitle) != null)
				title = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixTitle);

			// domain
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixDomain) != null) {
				String domain = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixDomain);
				for (int i = 0; i < domains.length; i++) {
					// if domain == expression, insert domain to all values that
					// have no domain
					if (domains[i] == PlotConfig.customPlotDomainExpression) {
						String expr = values[i];
						String[] sp = expr.split("\\:");

						String xd = sp[0];
						if (sp.length > 0)
							xd = sp[1];

						String[] split = xd.split("\\$");

						if (split.length < 2)
							continue;

						String expr2 = "";

						for (int j = 0; j < split.length; j++) {
							// even
							if ((j & 1) == 0) {
								expr2 += split[j];
								continue;
							}

							String[] split2 = split[j]
									.split(PlotConfig.customPlotDomainDelimiter);

							// if length = 1 -> no domain -> insert
							if (split2.length == 1) {
								expr2 += "$" + domain
										+ PlotConfig.customPlotDomainDelimiter
										+ split2[0] + "$";
							} else {
								// else, rebuild string and dont add domain
								expr2 += "$";
								for (int k = 0; k < split2.length; k++) {
									if (k == 0)
										expr2 += split2[k];
									else
										expr2 += PlotConfig.customPlotDomainDelimiter
												+ split2[k];
								}
								expr2 += "$";
							}
						}

						String xd2;
						if (sp.length > 0)
							xd2 = sp[0] + ":" + expr2;
						else
							xd2 = expr2;
						values[i] = xd2;
					}
				}
			}

			// labels
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixXLabel) != null)
				xLabel = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixXLabel);
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixYLabel) != null)
				yLabel = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixYLabel);

			// logscale
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixLogscale) != null)
				logscale = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixLogscale);

			// datetime
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixDatetime) != null)
				datetime = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixDatetime);

			// time data format
			if (Config.get(prefix + s
					+ PlotConfig.customPlotSuffixTimeDataFormat) != null)
				timeDataFormat = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixTimeDataFormat);

			// ranges
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixXRange) != null)
				xRange = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixXRange);
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixYRange) != null)
				yRange = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixYRange);

			// offsets
			try {
				xOffset = Config.getDouble(prefix + s
						+ PlotConfig.customPlotSuffixXOffset);
			} catch (NullPointerException | NumberFormatException e) {
			}
			try {
				yOffset = Config.getDouble(prefix + s
						+ PlotConfig.customPlotSuffixYOffset);
			} catch (NullPointerException | NumberFormatException e) {
			}

			// tics
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixXTics) != null)
				xTics = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixXTics);
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixYTics) != null)
				yTics = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixYTics);

			// scalings
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixXScaling) != null)
				xScaling = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixXScaling);
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixYScaling) != null)
				yScaling = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixYScaling);

			// as cdf
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixCdf) != null)
				plotAsCdf = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixCdf);

			// key
			String key = null;
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixKey) != null)
				key = Config.get(prefix + s + PlotConfig.customPlotSuffixKey);

			// style
			try {
				style = Config.getPlotStyle(prefix + s
						+ PlotConfig.customPlotSuffixPlotStyle);
			} catch (NullPointerException e) {
			}

			// value sort mode
			try {
				valueSortMode = Config.getValueSortMode(prefix + s
						+ PlotConfig.customPlotValueSortMode);
			} catch (NullPointerException e) {
			}

			// value sort list
			try {
				valueSortList = Config.keys(prefix + s
						+ PlotConfig.customPlotValueSortList);
			} catch (NullPointerException e) {
			}

			// dist plot type
			try {
				distPlotType = Config.getDistributionPlotType(prefix + s
						+ PlotConfig.customPlotSuffixDistType);
			} catch (NullPointerException e) {
			}

			// node value list sort orders
			try {
				order = Config.getNodeValueListOrder(prefix + s
						+ PlotConfig.customPlotSuffixNvlOrder);
			} catch (NullPointerException e) {
			}
			try {
				orderBy = Config.getNodeValueListOrderBy(prefix + s
						+ PlotConfig.customPlotSuffixNvlOrderBy);
			} catch (NullPointerException e) {
			}

			if (generalDomain == null
					&& (Config.get(prefix + s
							+ PlotConfig.customPlotSuffixDomain) != null)) {
				generalDomain = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixDomain);
			}

			if (generalDomain != null
					&& generalDomain.equals(PlotConfig.customPlotWildcard))
				plotAll = true;

			// Craft PlotConfig and add to configs list
			plotConfigs.add(new PlotConfig(filename, title, key, xLabel,
					yLabel, logscale, datetime, timeDataFormat, xOffset,
					yOffset, xRange, yRange, xTics, yTics, xScaling, yScaling,
					plotAsCdf, values, domains, style, valueSortMode,
					valueSortList, distPlotType, order, orderBy, plotAll,
					generalDomain));
		}
		return plotConfigs;
	}

	/** Generates and returns a dummy PlotConfig. **/
	public static PlotConfig generateDummyPlotConfig(String filename,
			String plotAsCdf, DistributionPlotType distPlotType,
			NodeValueListOrder order, NodeValueListOrderBy orderBy) {
		return new PlotConfig(filename, "", "", "", "", "", "", "", 0.0, 0.0,
				"", "", "", "", "", "", plotAsCdf, new String[0],
				new String[0], PlotStyle.linespoint, null, null, distPlotType,
				order, orderBy, false, "");
	}
}
