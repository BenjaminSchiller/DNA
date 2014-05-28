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
	private boolean plotAsCdf;
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
			double yOffset, String xRange, String yRange, boolean plotAsCdf,
			String[] values, String[] domains,
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

	public boolean isPlotAsCdf() {
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

	public String getGeneralDomain() {
		return generalDomain;
	}

	/** Returns the custom value plots created from config **/
	public static ArrayList<PlotConfig> getCustomValuePlots() {
		return PlotConfig.getCustomPlots(Config
				.get("CUSTOM_PLOT_PREFIX_VALUES"));
	}

	/** Returns the custom statistic plots created from config **/
	public static ArrayList<PlotConfig> getCustomStatisticPlots() {
		return PlotConfig.getCustomPlots(Config
				.get("CUSTOM_PLOT_PREFIX_STATISTICS"));
	}

	/** Returns the custom runtime plots created from config **/
	public static ArrayList<PlotConfig> getCustomRuntimePlots() {
		return PlotConfig.getCustomPlots(Config
				.get("CUSTOM_PLOT_PREFIX_RUNTIMES"));
	}

	/** Returns the custom metric values plots created from config **/
	public static ArrayList<PlotConfig> getCustomMetricValuePlots() {
		return PlotConfig.getCustomPlots(Config
				.get("CUSTOM_PLOT_PREFIX_METRIC_VALUES"));
	}

	/** Returns the custom metric distribution plots created from config **/
	public static ArrayList<PlotConfig> getCustomMetricDistributionPlots() {
		return PlotConfig.getCustomPlots(Config
				.get("CUSTOM_PLOT_PREFIX_METRIC_DISTRIBUTIONS"));
	}

	/** Returns the custom metric nodevaluelist plots created from config **/
	public static ArrayList<PlotConfig> getCustomMetricNodeValueListPlots() {
		return PlotConfig.getCustomPlots(Config
				.get("CUSTOM_PLOT_PREFIX_METRIC_NODEVALUELISTS"));
	}

	/** Returns the custom plots created from config **/
	private static ArrayList<PlotConfig> getCustomPlots(String prefix) {
		// define suffix
		String[] plots = Config.keys(prefix + "PLOTS");
		String valuesSuffix = "_VALUES";
		String cdfSuffix = "_CDF";
		String filenameSuffix = "_FILENAME";
		String titleSuffix = "_TITLE";
		String datetimeSuffix = "_DATETIME";
		String logscaleSuffix = "_LOGSCALE";
		String xLabelSuffix = "_XLABEL";
		String yLabelSuffix = "_YLABEL";
		String xOffsetSuffix = "_XOFFSET";
		String yOffsetSuffix = "_YOFFSET";
		String xRangeSuffix = "_XRANGE";
		String yRangeSuffix = "_YRANGE";
		String distTypeSuffix = "_TYPE";
		String nvlOrderSuffix = "_ORDER";
		String nvlOrderBySuffix = "_ORDERBY";

		// init list of configs
		ArrayList<PlotConfig> plotConfigs = new ArrayList<PlotConfig>(
				plots.length);

		for (String s : plots) {
			// get plot from config
			String keyword = s;
			String generalDomain = null;
			boolean plotAll = false;

			// set general domain
			if (prefix.equals(Config.get("CUSTOM_PLOT_PREFIX_STATISTICS")))
				generalDomain = Config.get("CUSTOM_PLOT_DOMAIN_STATISTICS");
			if (prefix.equals(Config.get("CUSTOM_PLOT_PREFIX_RUNTIMES")))
				generalDomain = Config.get("CUSTOM_PLOT_DOMAIN_RUNTIMES");

			// get values
			String[] values = Config.keys(prefix + s + valuesSuffix);

			// init domains arrays
			String[] domains = new String[values.length];

			// parse values and get domains
			for (int i = 0; i < values.length; i++) {
				String value = values[i];

				// check if function
				String[] split = value.split("=");
				if (split.length == 2) {
					// if function -> set domain and continue with next value
					domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_FUNCTION");
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
					domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_EXPRESSION");
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
					if (values[i].equals(Config.get("CUSTOM_PLOT_WILDCARD")))
						plotAll = true;

					// continue with next value
					continue;
				}

				// if value doesnt contain domain
				if (prefix.equals(Config.get("CUSTOM_PLOT_PREFIX_RUNTIMES")))
					domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_RUNTIMES");

				//
				if (prefix.equals(Config.get("CUSTOM_PLOT_PREFIX_STATISTICS")))
					domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_STATISTICS");

				if (value.equals(Config.get("CUSTOM_PLOT_WILDCARD")))
					plotAll = true;

				if (domains[i] == null)
					Log.warn("custom plot config parsing failure: '" + value
							+ "' has unknown domain!");
			}

			// read optional values from config
			boolean plotAsCdf = Config.getBoolean(prefix + s + cdfSuffix);

			// default values
			String filename = keyword;
			String xLabel = Config.get("GNUPLOT_XLABEL");
			String yLabel = Config.get("GNUPLOT_YLABEL");
			String logscale = null;
			String datetime = "";
			double xOffset = Config.getDouble("GNUPLOT_XOFFSET");
			double yOffset = Config.getDouble("GNUPLOT_YOFFSET");
			String xRange = Config.get("GNUPLOT_XRANGE");
			String yRange = Config.get("GNUPLOT_YRANGE");
			DistributionPlotType distPlotType = Config
					.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE");
			NodeValueListOrder order = Config
					.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER");
			NodeValueListOrderBy orderBy = Config
					.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY");

			// read config values
			// filename
			if (Config.get(prefix + s + filenameSuffix) != null)
				filename = Config.get(prefix + s + filenameSuffix);

			// title
			String title = filename;
			if (Config.get(prefix + s + titleSuffix) != null)
				title = Config.get(prefix + s + titleSuffix);

			// labels
			if (Config.get(prefix + s + xLabelSuffix) != null)
				xLabel = Config.get(prefix + s + xLabelSuffix);
			if (Config.get(prefix + s + yLabelSuffix) != null)
				yLabel = Config.get(prefix + s + yLabelSuffix);

			// logscale
			if (Config.get(prefix + s + logscaleSuffix) != null)
				logscale = Config.get(prefix + s + logscaleSuffix);

			// datetime
			if (Config.get(prefix + s + datetimeSuffix) != null)
				datetime = Config.get(prefix + s + datetimeSuffix);

			// ranges
			if (Config.get(prefix + s + xRangeSuffix) != null)
				xRange = Config.get(prefix + s + xRangeSuffix);
			if (Config.get(prefix + s + yRangeSuffix) != null)
				yRange = Config.get(prefix + s + yRangeSuffix);

			// offsets
			try {
				xOffset = Config.getDouble(prefix + s + xOffsetSuffix);
			} catch (NullPointerException | NumberFormatException e) {
			}
			try {
				yOffset = Config.getDouble(prefix + s + yOffsetSuffix);
			} catch (NullPointerException | NumberFormatException e) {
			}

			// dist plot type
			try {
				distPlotType = Config.getDistributionPlotType(prefix + s
						+ distTypeSuffix);
			} catch (NullPointerException e) {
			}

			// node value list sort orders
			try {
				order = Config.getNodeValueListOrder(prefix + s
						+ nvlOrderSuffix);
			} catch (NullPointerException e) {
			}
			try {
				orderBy = Config.getNodeValueListOrderBy(prefix + s
						+ nvlOrderBySuffix);
			} catch (NullPointerException e) {
			}

			// Craft PlotConfig and add to configs list
			plotConfigs.add(new PlotConfig(filename, title, xLabel, yLabel,
					logscale, datetime, xOffset, yOffset, xRange, yRange,
					plotAsCdf, values, domains, distPlotType, order, orderBy,
					plotAll, generalDomain));
		}
		return plotConfigs;
	}

}
