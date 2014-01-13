package dna.visualization.config;

import java.util.ArrayList;

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
	private MetricVisualizerItem insertAllGeneralRuntimes;
	private MetricVisualizerItem insertAllMetricRuntimes;
	private MetricVisualizerItem insertAllStatistics;
	private MultiScalarDistributionItem insertAllDistributions;
	private MultiScalarNodeValueListItem insertAllNodeValueLists;

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

	public void setAllMetrics(MetricVisualizerItem c) {
		this.insertAllMetrics = c;
	}

	public void setAllGeneralRuntimes(MetricVisualizerItem c) {
		this.insertAllGeneralRuntimes = c;
	}

	public void setAllMetricRuntimes(MetricVisualizerItem c) {
		this.insertAllMetricRuntimes = c;
	}

	public void setAllStatistics(MetricVisualizerItem c) {
		this.insertAllStatistics = c;
	}

	public void setAllDistributions(MultiScalarDistributionItem c) {
		this.insertAllDistributions = c;
	}

	public void setAllNodeValueLists(MultiScalarNodeValueListItem c) {
		this.insertAllNodeValueLists = c;
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
}
