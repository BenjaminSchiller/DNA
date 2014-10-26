package dna.visualization.config;

import java.awt.Color;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

/**
 * General configuration item object which allows to configure the yAxis
 * orientation (y1 or y2), default visibility option (shown or hidden), display
 * mode (linespoint or bars) and color for an item, usually identified by the
 * name. In addition it is possible to define an Integer "orderId", which by
 * default is -1 meaning the actual order in the list doesnt matter. Positive
 * values represent and actual order of how the values will be added. (0 before
 * 1, 1 before 2 and so on.) For more specific configuration objects see
 * MetricVisualizerItem or the MultiScalarDistributionItem /
 * MultiScalarNodeValueListItem.
 * 
 * @author Rwilmes
 */
public class ConfigItem {
	/*
	 * HARD CODED DEFAULTS
	 */
	// metric visualizer
	public static final DisplayMode metricVisualizerDefaultDisplayMode = DisplayMode.linespoint;
	public static final yAxisSelection metricVisualizerDefaultYAxisSelection = yAxisSelection.y1;
	public static final GraphVisibility metricVisualizerDefaultGraphVisibility = GraphVisibility.shown;

	// multi scalar visualizer
	public static final GraphVisibility multiScalarVisualizerDefaultGraphVisibility = GraphVisibility.shown;

	// distributions
	public static final DisplayMode multiScalarVisualizerDefaultDistributionDisplayMode = DisplayMode.bars;
	public static final SortModeDist multiScalarVisualizerDefaultDistributionSortMode = SortModeDist.distribution;
	public static final xAxisSelection multiScalarVisualizerDefaultDistXAxisSelection = xAxisSelection.x1;
	public static final yAxisSelection multiScalarVisualizerDefaultDistYAxisSelection = yAxisSelection.y1;

	// nodevaluelists
	public static final DisplayMode multiScalarVisualizerDefaultNodeValueListDisplayMode = DisplayMode.linespoint;
	public static final SortModeNVL multiScalarVisualizerDefaultNodeValueListSortMode = SortModeNVL.ascending;
	public static final xAxisSelection multiScalarVisualizerDefaultNVLXAxisSelection = xAxisSelection.x2;
	public static final yAxisSelection multiScalarVisualizerDefaultNVLYAxisSelection = yAxisSelection.y2;

	// class variables
	protected String name;
	protected yAxisSelection yAxis;
	protected GraphVisibility visibility;
	protected DisplayMode displayMode;

	protected Color color;

	protected int orderId;

	public ConfigItem(String name) {
		this(name, DisplayMode.linespoint, yAxisSelection.y1,
				GraphVisibility.shown, -1);
	}

	public ConfigItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility) {
		this(name, displayMode, yAxis, visibility, -1);
	}

	public ConfigItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, int orderId) {
		this.name = name;
		this.displayMode = displayMode;
		this.yAxis = yAxis;
		this.visibility = visibility;
		this.orderId = orderId;
	}

	public ConfigItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, Color color) {
		this(name, displayMode, yAxis, visibility, color, -1);
	}

	public ConfigItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, Color color,
			int orderId) {
		this.name = name;
		this.displayMode = displayMode;
		this.yAxis = yAxis;
		this.visibility = visibility;
		this.color = color;
		this.orderId = orderId;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	public int getOrderId() {
		return this.orderId;
	}
}
