package dna.visualization.config;

import java.awt.Color;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

public class ConfigItem {
	protected String name;
	protected yAxisSelection yAxis;
	protected GraphVisibility visibility;
	protected DisplayMode displayMode;

	protected Color color;

	public ConfigItem(String name) {
		this(name, DisplayMode.linespoint, yAxisSelection.y1,
				GraphVisibility.shown);
	}

	public ConfigItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility) {
		this.name = name;
		this.displayMode = displayMode;
		this.yAxis = yAxis;
		this.visibility = visibility;
	}

	public ConfigItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, Color color) {
		this.name = name;
		this.displayMode = displayMode;
		this.yAxis = yAxis;
		this.visibility = visibility;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public DisplayMode getDisplayMode() {
		return this.displayMode;
	}

	public yAxisSelection getYAxis() {
		return this.yAxis;
	}

	public GraphVisibility getVisibility() {
		return this.visibility;
	}

	public Color getColor() {
		return this.color;
	}
}
