package dna.visualization.config;

import java.util.ArrayList;

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

}
