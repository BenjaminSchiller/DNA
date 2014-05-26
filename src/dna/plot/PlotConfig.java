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
	private String[] values;
	private String[] domains;
	private boolean plotAsCdf;
	private DistributionPlotType distPlotType;
	private NodeValueListOrder order;
	private NodeValueListOrderBy orderBy;
	private boolean plotAll;

	// constructor
	private PlotConfig(String name, boolean plotAsCdf, String[] values,
			String[] domains, DistributionPlotType distPlotType,
			NodeValueListOrder order, NodeValueListOrderBy orderBy,
			boolean plotAll) {
		this.name = name;
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
		String[] plots = Config.keys(prefix
				+ Config.get("CUSTOM_PLOT_SUFFIX_PLOTS"));
		String nameSuffix = Config.get("CUSTOM_PLOT_SUFFIX_NAME");
		String valuesSuffix = Config.get("CUSTOM_PLOT_SUFFIX_VALUES");
		String cdfSuffix = Config.get("CUSTOM_PLOT_SUFFIX_CDF");

		boolean plotAll = false;

		ArrayList<PlotConfig> plotConfigs = new ArrayList<PlotConfig>(
				plots.length);

		for (String s : plots) {
			String name = Config.get(prefix + s + nameSuffix);
			String[] values = Config.keys(prefix + s + valuesSuffix);
			String[] domains = new String[values.length];
			DistributionPlotType distPlotType = Config
					.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE");
			NodeValueListOrder order = Config
					.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER");
			NodeValueListOrderBy orderBy = Config
					.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY");

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
					if (values[i].equals(Config.get("CUSTOM_PLOT_WILDCARD")))
						plotAll = true;
				}
			} else if (prefix.equals(Config
					.get("CUSTOM_PLOT_PREFIX_STATISTICS"))) {
				// if statistics plot, add statistics domain
				for (int i = 0; i < values.length; i++) {
					domains[i] = Config.get("CUSTOM_PLOT_DOMAIN_STATISTICS");

					// check for wildcard
					if (values[i].equals(Config.get("CUSTOM_PLOT_WILDCARD")))
						plotAll = true;
				}
			} else {
				// get domains from strings
				for (int i = 0; i < values.length; i++) {
					String[] split = values[i].split("\\.");
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

					if (values[i].equals(Config.get("CUSTOM_PLOT_WILDCARD")))
						plotAll = true;
				}
			}
			boolean plotAsCdf = Config.getBoolean(prefix + s + cdfSuffix);

			try {
				distPlotType = Config.getDistributionPlotType(prefix + s
						+ Config.get("CUSTOM_PLOT_SUFFIX_DIST_TYPE"));
			} catch (NullPointerException e) {
			}

			try {
				order = Config.getNodeValueListOrder(prefix + s
						+ Config.get("CUSTOM_PLOT_SUFFIX_NVL_ORDER"));
			} catch (NullPointerException e) {
			}

			try {
				orderBy = Config.getNodeValueListOrderBy(prefix + s
						+ Config.get("CUSTOM_PLOT_SUFFIX_NVL_ORDERBY"));
			} catch (NullPointerException e) {
			}

			// Craft PlotConfig and add to configs list
			plotConfigs.add(new PlotConfig(name, plotAsCdf, values, domains,
					distPlotType, order, orderBy, plotAll));
		}
		return plotConfigs;
	}

}
