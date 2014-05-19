package dna.plot;

import java.util.ArrayList;

import dna.util.Config;
import dna.util.Log;

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

	// constructor
	private PlotConfig(String name, boolean plotAsCdf, String[] values,
			String[] domains) {
		this.name = name;
		this.plotAsCdf = plotAsCdf;
		this.values = values;
		this.domains = domains;
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
		Log.info("reading plot configs with prefix '" + prefix + "'");
		String nameSuffix = Config.get("CUSTOM_PLOT_SUFFIX_NAME");
		String valuesSuffix = Config.get("CUSTOM_PLOT_SUFFIX_VALUES");
		String cdfSuffix = Config.get("CUSTOM_PLOT_SUFFIX_CDF");

		ArrayList<PlotConfig> plotConfigs = new ArrayList<PlotConfig>(
				plots.length);

		for (String s : plots) {
			System.out.println(prefix + s + valuesSuffix);
			String name = Config.get(prefix + s + nameSuffix);
			String[] values = Config.keys(prefix + s + valuesSuffix);
			String[] domains = new String[values.length];
			if (prefix.equals(Config.get("CUSTOM_PLOT_PREFIX_RUNTIMES"))) {
				for (int i = 0; i < values.length; i++) {
					domains[i] = Config.get("PLOT_CUSTOM_RUNTIME");
				}
			} else if (prefix.equals(Config.get("CUSTOM_PLOT_PREFIX_VALUES"))) {
				for (int i = 0; i < values.length; i++) {
					String[] split = values[i].split("\\.");
					domains[i] = split[0];
					String value = "";
					for (int j = 1; j < split.length; j++) {
						value += split[j];
					}
					values[i] = value;
				}
			}
			boolean plotAsCdf = Config.getBoolean(prefix + s + cdfSuffix);
			plotConfigs.add(new PlotConfig(name, plotAsCdf, values, domains));
		}
		return plotConfigs;
	}

}
