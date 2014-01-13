package dna.visualization.config;

import java.awt.Color;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

/**
 * Specialized ConfigItem object for all nodevaluelists that can be used in the
 * MultiScalar-Visualizer.
 * 
 * @author Rwilmes
 */
public class MultiScalarNodeValueListItem extends ConfigItem {

	private SortModeNVL sortMode;
	private xAxisSelection xAxis;

	public MultiScalarNodeValueListItem(String name) {
		super(name);
		this.sortMode = SortModeNVL.ascending;
		this.xAxis = xAxisSelection.x1;
	}

	public MultiScalarNodeValueListItem(String name, SortModeNVL sortMode,
			xAxisSelection xAxis, yAxisSelection yAxis,
			DisplayMode displayMode, GraphVisibility visibility) {
		super(name, DisplayMode.linespoint, yAxis, visibility);
		this.sortMode = sortMode;
		this.xAxis = xAxis;
	}

	public MultiScalarNodeValueListItem(String name, SortModeNVL sortMode,
			xAxisSelection xAxis, yAxisSelection yAxis,
			DisplayMode displayMode, GraphVisibility visibility, Color color) {
		super(name, DisplayMode.linespoint, yAxis, visibility, color);
		this.sortMode = sortMode;
		this.xAxis = xAxis;
	}

	public SortModeNVL getSortMode() {
		return this.sortMode;
	}

	public xAxisSelection getXAxis() {
		return this.xAxis;
	}
}
