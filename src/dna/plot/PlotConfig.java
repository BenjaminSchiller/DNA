package dna.plot;

import java.util.ArrayList;

import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
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

	public static String customPlotSuffixLogscale = "_LOGSCALE";

	public static String customPlotSuffixXLabel = "_XLABEL";

	public static String customPlotSuffixYLabel = "_YLABEL";

	public static String customPlotSuffixXOffset = "_XOFFSET";

	public static String customPlotSuffixYOffset = "_YOFFSET";

	public static String customPlotSuffixXRange = "_XRANGE";

	public static String customPlotSuffixYRange = "_YRANGE";

	public static String customPlotSuffixXTics = "_XTICS";

	public static String customPlotSuffixYTics = "_YTICS";

	public static String customPlotSuffixDistType = "_TYPE";

	public static String customPlotSuffixNvlOrder = "_ORDER";

	public static String customPlotSuffixNvlOrderBy = "_ORDERBY";

	// DOMAINS
	public static String customPlotDomainStatistics = "statistics";

	public static String customPlotDomainRuntimes = "runtimes";

	public static String customPlotDomainMetricRuntimes = "metric_runtimes";

	public static String customPlotDomainGeneralRuntimes = "general_runtimes";

	public static String customPlotDomainFunction = "function";

	public static String customPlotDomainExpression = "expression";

	// WILDCARD
	public static String customPlotWildcard = "*";

	// variables
	private String filename;
	private String title;
	private String xLabel;
	private String yLabel;
	private String logscale;
	private String datetime;
	private double xOffset;
	private double yOffset;
	private String xRange;
	private String yRange;
	private String xTics;
	private String yTics;
	private String plotAsCdf;
	private String[] values;
	private String[] domains;
	private DistributionPlotType distPlotType;
	private NodeValueListOrder order;
	private NodeValueListOrderBy orderBy;
	private boolean plotAll;
	private String generalDomain;

	// constructor
	private PlotConfig(String filename, String title, String xLabel,
			String yLabel, String logscale, String datetime, double xOffset,
			double yOffset, String xRange, String yRange, String xTics,
			String yTics, String plotAsCdf, String[] values, String[] domains,
			DistributionPlotType distPlotType, NodeValueListOrder order,
			NodeValueListOrderBy orderBy, boolean plotAll, String generalDomain) {
		this.filename = filename;
		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.logscale = logscale;
		this.datetime = datetime;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xRange = xRange;
		this.yRange = yRange;
		this.xTics = xTics;
		this.yTics = yTics;
		this.plotAsCdf = plotAsCdf;
		this.values = values;
		this.domains = domains;
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

	public String getGeneralDomain() {
		return generalDomain;
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

					// if expression -> set domain and continue with next value
					domains[i] = PlotConfig.customPlotDomainExpression;
					continue;
				}

				// check if contains domain
				split = value.split("\\.");
				if (split.length > 1) {
					String domain = "";
					for (int j = 0; j < split.length - 1; j++) {
						if (j == 0)
							domain += split[j];
						else
							domain += "." + split[j];
					}
					// take first part as domain
					domains[i] = domain;

					// take last part as value name
					values[i] = split[split.length - 1];

					// check for wildcard
					if (values[i].equals(PlotConfig.customPlotWildcard))
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

				if (domains[i] == null)
					Log.warn("custom plot config parsing failure: '" + value
							+ "' has unknown domain!");
			}

			// read optional values from config
			String plotAsCdf = "false";

			// default values
			String filename = keyword;
			String xLabel = Config.get("GNUPLOT_XLABEL");
			String yLabel = Config.get("GNUPLOT_YLABEL");
			String logscale = null;
			String datetime = null;
			double xOffset = Config.getDouble("GNUPLOT_XOFFSET");
			double yOffset = Config.getDouble("GNUPLOT_YOFFSET");
			String xRange = Config.get("GNUPLOT_XRANGE");
			String yRange = Config.get("GNUPLOT_YRANGE");
			String xTics = null;
			String yTics = null;
			DistributionPlotType distPlotType = Config
					.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE");
			NodeValueListOrder order = Config
					.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER");
			NodeValueListOrderBy orderBy = Config
					.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY");

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

			// as cdf
			if (Config.get(prefix + s + PlotConfig.customPlotSuffixCdf) != null)
				plotAsCdf = Config.get(prefix + s
						+ PlotConfig.customPlotSuffixCdf);

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

			// Craft PlotConfig and add to configs list
			plotConfigs.add(new PlotConfig(filename, title, xLabel, yLabel,
					logscale, datetime, xOffset, yOffset, xRange, yRange,
					xTics, yTics, plotAsCdf, values, domains, distPlotType,
					order, orderBy, plotAll, generalDomain));
		}
		return plotConfigs;
	}

}
