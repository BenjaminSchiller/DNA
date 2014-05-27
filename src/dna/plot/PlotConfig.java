package dna.plot;

import java.util.ArrayList;

import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.util.Config;

/**
 * Configuration object representing one plot which will be read from properties
 * config file.
 * 
 * @author RWilmes
 */
public class PlotConfig {
	private String name;
	private String filename;
	private String title;
	private String xLabel;
	private String yLabel;
	private String logscale;
	private String datetime;
	private double xOffset;
	private double yOffset;
	private boolean plotAsCdf;
	private String[] values;
	private String[] domains;
	private DistributionPlotType distPlotType;
	private NodeValueListOrder order;
	private NodeValueListOrderBy orderBy;
	private boolean plotAll;

	// constructor
	private PlotConfig(String name, String filename, String title,
			String xLabel, String yLabel, String logscale, String datetime,
			double xOffset, double yOffset, boolean plotAsCdf, String[] values,
			String[] domains, DistributionPlotType distPlotType,
			NodeValueListOrder order, NodeValueListOrderBy orderBy,
			boolean plotAll) {
		this.name = name;
		this.filename = filename;
		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.logscale = logscale;
		this.datetime = datetime;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.plotAsCdf = plotAsCdf;
		this.values = values;
		this.domains = domains;
		this.distPlotType = distPlotType;
		this.order = order;
		this.orderBy = orderBy;
		this.plotAll = plotAll;
	}

	// getters
	public String getName() {
		return name;
	}

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
		// get suffix from config
		String[] plots = Config.keys(prefix
				+ Config.get("CUSTOM_PLOT_SUFFIX_PLOTS"));
		String nameSuffix = Config.get("CUSTOM_PLOT_SUFFIX_NAME");
		String valuesSuffix = Config.get("CUSTOM_PLOT_SUFFIX_VALUES");
		String cdfSuffix = Config.get("CUSTOM_PLOT_SUFFIX_CDF");
		String filenameSuffix = Config.get("CUSTOM_PLOT_SUFFIX_FILENAME");
		String titleSuffix = Config.get("CUSTOM_PLOT_SUFFIX_TITLE");
		String datetimeSuffix = Config.get("CUSTOM_PLOT_SUFFIX_DATETIME");
		String logscaleSuffix = Config.get("CUSTOM_PLOT_SUFFIX_LOGSCALE");
		String xLabelSuffix = Config.get("CUSTOM_PLOT_SUFFIX_XLABEL");
		String yLabelSuffix = Config.get("CUSTOM_PLOT_SUFFIX_YLABEL");
		String xOffsetSuffix = Config.get("CUSTOM_PLOT_SUFFIX_XOFFSET");
		String yOffsetSuffix = Config.get("CUSTOM_PLOT_SUFFIX_YOFFSET");
		String distTypeSuffix = Config.get("CUSTOM_PLOT_SUFFIX_DIST_TYPE");
		String nvlOrderSuffix = Config.get("CUSTOM_PLOT_SUFFIX_NVL_ORDER");
		String nvlOrderBySuffix = Config.get("CUSTOM_PLOT_SUFFIX_NVL_ORDERBY");

		// init list of configs
		ArrayList<PlotConfig> plotConfigs = new ArrayList<PlotConfig>(
				plots.length);

		for (String s : plots) {
			// get plot from config
			String name = Config.get(prefix + s + nameSuffix);
			boolean plotAll = false;
			String[] values = Config.keys(prefix + s + valuesSuffix);
			String[] domains = new String[values.length];

			// get domains
			if (prefix.equals(Config.get("CUSTOM_PLOT_PREFIX_RUNTIMES"))) {
				String metricRuntimeDomain = Config
						.get("CUSTOM_PLOT_DOMAIN_METRICRUNTIMES");
				String generalRuntimeDomain = Config
						.get("CUSTOM_PLOT_DOMAIN_GENERALRUNTIMES");
				String runtimeDomain = Config
						.get("CUSTOM_PLOT_DOMAIN_RUNTIMES");
				// if runtimes plot, add runtimes domain
				for (int i = 0; i < values.length; i++) {

					// check if function
					String[] functionSplit = values[i].split("=");
					if (functionSplit.length > 1) {
						domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_FUNCTION");
					} else {
						// not a function
						String[] split = values[i].split("\\.");
						if (!split[0].equals(runtimeDomain)
								&& !split[0].equals(metricRuntimeDomain)
								&& !split[0].equals(generalRuntimeDomain)) {
							domains[i] = runtimeDomain;
						} else {
							domains[i] = split[0];
							String domain = "";
							for (int j = 0; j < split.length - 1; j++) {
								if (j == 0)
									domain += split[j];
								else
									domain += "." + split[j];
							}
							domains[i] = domain;
							values[i] = split[split.length - 1];
						}

						// check for wildcard
						if (values[i]
								.equals(Config.get("CUSTOM_PLOT_WILDCARD")))
							plotAll = true;
					}
				}
			} else if (prefix.equals(Config
					.get("CUSTOM_PLOT_PREFIX_STATISTICS"))) {
				// if statistics plot, add statistics domain
				for (int i = 0; i < values.length; i++) {
					// check if function
					String[] functionSplit = values[i].split("=");
					if (functionSplit.length > 1) {
						domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_FUNCTION");
					} else {
						// not a function
						domains[i] = Config
								.get("CUSTOM_PLOT_DOMAIN_STATISTICS");
						String[] split = values[i].split("\\.");
						if (split[0].equals(Config
								.get("CUSTOM_PLOT_DOMAIN_STATISTICS"))) {
							String domain = "";
							for (int j = 0; j < split.length - 1; j++) {
								if (j == 0)
									domain += split[j];
								else
									domain += "." + split[j];
							}
							domains[i] = domain;
							values[i] = split[split.length - 1];
						}

						// check for wildcard
						if (values[i]
								.equals(Config.get("CUSTOM_PLOT_WILDCARD")))
							plotAll = true;
					}
				}
			} else {
				// get domains from strings
				for (int i = 0; i < values.length; i++) {
					// check if function
					String[] functionSplit = values[i].split("=");
					if (functionSplit.length > 1) {
						domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_FUNCTION");
					} else {
						// not a function
						String[] split = values[i].split("\\.");
						domains[i] = split[0];
						String domain = "";
						for (int j = 0; j < split.length - 1; j++) {
							if (j == 0)
								domain += split[j];
							else
								domain += "." + split[j];
						}
						values[i] = split[split.length - 1];
						domains[i] = domain;

						if (values[i]
								.equals(Config.get("CUSTOM_PLOT_WILDCARD")))
							plotAll = true;
					}
				}
			}

			// read optional values from config
			boolean plotAsCdf = Config.getBoolean(prefix + s + cdfSuffix);

			// default values
			String filename = name;
			String title = filename;
			String xLabel = Config.get("GNUPLOT_XLABEL");
			String yLabel = Config.get("GNUPLOT_YLABEL");
			String logscale = null;
			String datetime = "";
			double xOffset = Config.getDouble("GNUPLOT_XOFFSET");
			double yOffset = Config.getDouble("GNUPLOT_YOFFSET");
			DistributionPlotType distPlotType = Config
					.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE");
			NodeValueListOrder order = Config
					.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER");
			NodeValueListOrderBy orderBy = Config
					.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY");

			// try to get config values
			try {
				filename = Config.get(prefix + s + filenameSuffix);
			} catch (NullPointerException e) {
			}

			try {
				title = Config.get(prefix + s + titleSuffix);
			} catch (NullPointerException e) {
			}

			try {
				xLabel = Config.get(prefix + s + xLabelSuffix);
			} catch (NullPointerException e) {
			}

			try {
				yLabel = Config.get(prefix + s + yLabelSuffix);
			} catch (NullPointerException e) {
			}

			try {
				logscale = Config.get(prefix + s + logscaleSuffix);
			} catch (NullPointerException e) {
			}

			try {
				datetime = Config.get(prefix + s + datetimeSuffix);
			} catch (NullPointerException e) {
			}

			try {
				xOffset = Config.getDouble(prefix + s + xOffsetSuffix);
			} catch (NullPointerException | NumberFormatException e) {
			}

			try {
				yOffset = Config.getDouble(prefix + s + yOffsetSuffix);
			} catch (NullPointerException | NumberFormatException e) {
			}

			try {
				distPlotType = Config.getDistributionPlotType(prefix + s
						+ distTypeSuffix);
			} catch (NullPointerException e) {
			}

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
			plotConfigs.add(new PlotConfig(name, filename, title, xLabel,
					yLabel, logscale, datetime, xOffset, yOffset, plotAsCdf,
					values, domains, distPlotType, order, orderBy, plotAll));
		}
		return plotConfigs;
	}

}
