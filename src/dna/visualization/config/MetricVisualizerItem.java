package dna.visualization.config;

import java.awt.Color;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

public class MetricVisualizerItem extends ConfigItem {

	public MetricVisualizerItem(String name) {
		super(name);
	}

	public MetricVisualizerItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility) {
		super(name, displayMode, yAxis, visibility);
	}

	public MetricVisualizerItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, Color color) {
		super(name, displayMode, yAxis, visibility, color);
	}
}
