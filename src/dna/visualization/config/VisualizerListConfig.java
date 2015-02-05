package dna.visualization.config;

import java.util.ArrayList;

import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration-Object that contains several configuration items, which hold
 * parameters for configuration of single values or for a specific sort of
 * values. For example: If a single MetricVisualizerItem-configuration object is
 * added via addConfigItem(..), it will add the metric value, specified by its
 * name, to the chart on initialization. If on the other hand the
 * setAllGeneralRuntimes(..) method is used with a
 * MetricVisualizerItem-configuration object, all general runtimes will be added
 * upon initialization, using the properties of the given configuration object.
 * Note: General config items dont need a name.
 * 
 * @author Rwilmes
 */
public class VisualizerListConfig {

	// enumerations for configuration
	public enum xAxisSelection {
		x1, x2
	};

	public enum yAxisSelection {
		y1, y2
	};

	public enum GraphVisibility {
		shown, hidden
	}

	public enum DisplayMode {
		linespoint, bars
	}

	public enum SortModeNVL {
		index, ascending, descending
	};

	public enum SortModeDist {
		distribution, cdf
	}

	// entry array list
	private ArrayList<ConfigItem> entries;

	// rules
	private MetricVisualizerItem insertAllMetrics;
	private int metricsOrderId;

	private MetricVisualizerItem insertAllGeneralRuntimes;
	private int generalRuntimesOrderId;

	private MetricVisualizerItem insertAllMetricRuntimes;
	private int metricRuntimesOrderId;

	private MetricVisualizerItem insertAllStatistics;
	private int statisticsOrderId;

	private MultiScalarDistributionItem insertAllDistributions;
	private int distributionsOrderId;

	private MultiScalarNodeValueListItem insertAllNodeValueLists;
	private int nodeValueListsOrderId;

	// statics
	public static final String generalMetricsConfigName = "GENERAL_METRICS_CONFIG_NAME_IDENTIFIER";
	public static final String generalGeneralRuntimesConfigName = "GENERAL_GENERAL_RUNTIMES_CONFIG_NAME_IDENTIFIER";
	public static final String generalMetricRuntimesConfigName = "GENERAL_METRIC_RUNTIMES_CONFIG_NAME_IDENTIFIER";
	public static final String generalStatisticsConfigName = "GENERAL_STATISTICS_CONFIG_NAME_IDENTIFIER";
	public static final String generalDistributionsConfigName = "GENERAL_DISTRIBUTIONS_CONFIG_NAME_IDENTIFIER";
	public static final String generalNodeValueListsConfigName = "GENERAL_NODEVALUELISTS_CONFIG_NAME_IDENTIFIER";

	// constructor
	public VisualizerListConfig() {
		this.entries = new ArrayList<ConfigItem>();
	}

	// methods
	public void addConfigItem(ConfigItem item) {
		this.entries.add(item);
	}

	public void addConfigItems(ConfigItem[] items) {
		for (int i = 0; i < items.length; i++) {
			this.addConfigItem(items[i]);
		}
	}

	public void removeConfigItem(ConfigItem item) {
		this.entries.remove(item);
	}

	public ArrayList<ConfigItem> getEntries() {
		return this.entries;
	}

	public void setAllMetrics(MetricVisualizerItem c, int orderId) {
		this.insertAllMetrics = c;
		this.metricsOrderId = orderId;
	}

	public void setAllGeneralRuntimes(MetricVisualizerItem c, int orderId) {
		this.insertAllGeneralRuntimes = c;
		this.generalRuntimesOrderId = orderId;
	}

	public void setAllMetricRuntimes(MetricVisualizerItem c, int orderId) {
		this.insertAllMetricRuntimes = c;
		this.metricRuntimesOrderId = orderId;
	}

	public void setAllStatistics(MetricVisualizerItem c, int orderId) {
		this.insertAllStatistics = c;
		this.statisticsOrderId = orderId;
	}

	public void setAllDistributions(MultiScalarDistributionItem c, int orderId) {
		this.insertAllDistributions = c;
		this.distributionsOrderId = orderId;
	}

	public void setAllNodeValueLists(MultiScalarNodeValueListItem c, int orderId) {
		this.insertAllNodeValueLists = c;
		this.nodeValueListsOrderId = orderId;
	}

	/** returns true if any of the generalconfiguration items is set **/
	public boolean isAnyGeneralConfigSet() {
		if (this.insertAllMetrics != null)
			return true;
		if (this.insertAllGeneralRuntimes != null)
			return true;
		if (this.insertAllMetricRuntimes != null)
			return true;
		if (this.insertAllStatistics != null)
			return true;
		if (this.insertAllDistributions != null)
			return true;
		if (this.insertAllNodeValueLists != null)
			return true;
		return false;
	}

	public MetricVisualizerItem getAllMetricsConfig() {
		return this.insertAllMetrics;
	}

	public MetricVisualizerItem getAllGeneralRuntimesConfig() {
		return this.insertAllGeneralRuntimes;
	}

	public MetricVisualizerItem getAllMetricRuntimesConfig() {
		return this.insertAllMetricRuntimes;
	}

	public MetricVisualizerItem getAllStatisticsConfig() {
		return this.insertAllStatistics;
	}

	public MultiScalarDistributionItem getAllDistributionsConfig() {
		return this.insertAllDistributions;
	}

	public MultiScalarNodeValueListItem getAllNodeValueListsConfig() {
		return this.insertAllNodeValueLists;
	}

	public int getMetricsOrderId() {
		return this.metricsOrderId;
	}

	public int getGeneralRuntimesOrderId() {
		return this.generalRuntimesOrderId;
	}

	public int getMetricRuntimesOrderId() {
		return this.metricRuntimesOrderId;
	}

	public int getStatisticsOrderId() {
		return this.statisticsOrderId;
	}

	public int getDistributionsOrderId() {
		return this.distributionsOrderId;
	}

	public int getNodeValueListsOrderId() {
		return this.nodeValueListsOrderId;
	}

	/** creates a visualizerlistconfig from a json object **/
	public static VisualizerListConfig createConfigFromJSONObject(JSONObject o) {
		VisualizerListConfig config = new VisualizerListConfig();
		JSONObject visConfig = o.getJSONObject("VisualizerConfig");

		for (String config1 : JSONObject.getNames(visConfig)) {
			switch (config1) {
			case "GeneralConfigs":
				config.addGeneralConfigsFromJSONObject(visConfig
						.getJSONObject(config1));
				break;
			case "SingleConfigs":
				config.addSingleConfigsFromJSONObject(visConfig
						.getJSONObject(config1));
				break;
			}
		}

		return config;
	}

	/** adds general configs to a visualizerlistconfig from a json object **/
	public void addGeneralConfigsFromJSONObject(JSONObject o) {
		for (int i = 0; i < JSONObject.getNames(o).length; i++) {
			String config = JSONObject.getNames(o)[i];
			MetricVisualizerItem tempItem;
			int tempId = -1;
			switch (config) {
			case "generalMetricConfig":
				if (o.getJSONObject(config).has("orderId")) {
					tempId = o.getJSONObject(config).getInt("orderId");
				}
				tempItem = MetricVisualizerItem
						.createMetricVisualizerItemFromJSONObject(o
								.getJSONObject(config));
				tempItem.setName(VisualizerListConfig.generalMetricsConfigName);
				this.setAllMetrics(tempItem, tempId);
				break;
			case "generalStatisticsConfig":
				if (o.getJSONObject(config).has("orderId")) {
					tempId = o.getJSONObject(config).getInt("orderId");
				}
				tempItem = MetricVisualizerItem
						.createMetricVisualizerItemFromJSONObject(o
								.getJSONObject(config));
				tempItem.setName(VisualizerListConfig.generalStatisticsConfigName);
				this.setAllStatistics(tempItem, tempId);
				break;
			case "generalGeneralRuntimesConfig":
				if (o.getJSONObject(config).has("orderId")) {
					tempId = o.getJSONObject(config).getInt("orderId");
				}
				tempItem = MetricVisualizerItem
						.createMetricVisualizerItemFromJSONObject(o
								.getJSONObject(config));
				tempItem.setName(VisualizerListConfig.generalGeneralRuntimesConfigName);
				this.setAllGeneralRuntimes(tempItem, tempId);
				break;
			case "generalMetricRuntimesConfig":
				if (o.getJSONObject(config).has("orderId")) {
					tempId = o.getJSONObject(config).getInt("orderId");
				}
				tempItem = MetricVisualizerItem
						.createMetricVisualizerItemFromJSONObject(o
								.getJSONObject(config));
				tempItem.setName(VisualizerListConfig.generalMetricRuntimesConfigName);
				this.setAllMetricRuntimes(tempItem, tempId);
				break;
			case "generalDistributionConfig":
				if (o.getJSONObject(config).has("orderId")) {
					tempId = o.getJSONObject(config).getInt("orderId");
				}
				MultiScalarDistributionItem tempItem2 = MultiScalarDistributionItem
						.createMultiScalarDistributionItemFromJSONObject(o
								.getJSONObject(config));
				tempItem2
						.setName(VisualizerListConfig.generalDistributionsConfigName);
				this.setAllDistributions(tempItem2, tempId);
				break;
			case "generalNodeValueListConfig":
				if (o.getJSONObject(config).has("orderId")) {
					tempId = o.getJSONObject(config).getInt("orderId");
				}
				MultiScalarNodeValueListItem tempItem3 = MultiScalarNodeValueListItem
						.createMultiScalarNodeValueListItemFromJSONObject(o
								.getJSONObject(config));
				tempItem3
						.setName(VisualizerListConfig.generalNodeValueListsConfigName);
				this.setAllNodeValueLists(tempItem3, tempId);
				break;
			}
		}
	}

	/** adds single configs to a visualizerlistconfig from a json object **/
	public void addSingleConfigsFromJSONObject(JSONObject o) {
		for (String config : JSONObject.getNames(o)) {
			JSONObject tempObject = o.getJSONObject(config);

			switch (tempObject.getString("Type")) {
			case "MetricVisualizerItem":
				this.addConfigItem(MetricVisualizerItem
						.createMetricVisualizerItemFromJSONObject(tempObject));
				break;
			case "MultiScalarDistributionItem":
				this.addConfigItem(MultiScalarDistributionItem
						.createMultiScalarDistributionItemFromJSONObject(tempObject));
				break;
			case "MultiScalarNodeValueListItem":
				this.addConfigItem(MultiScalarNodeValueListItem
						.createMultiScalarNodeValueListItemFromJSONObject(tempObject));
				break;
			}
		}
	}
}
