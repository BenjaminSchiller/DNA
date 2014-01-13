package dna.visualization.config;

import java.awt.Color;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

/**
 * General configuration item object which allows to configure the yAxis
 * orientation (y1 or y2), default visibility option (shown or hidden), display
 * mode (linespoint or bars) and color for an item, usually identified by the
 * name. For more specific configuration objects see MetricVisualizerItem or the
 * MultiScalarDistributionItem / MultiScalarNodeValueListItem.
 * 
 * @author Rwilmes
 */
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
