package dna.visualization.config;

import java.awt.Color;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

/**
 * Specialized ConfigItem object for all distributions that can be used in the
 * MultiScalar-Visualizer.
 * 
 * @author Rwilmes
 */
public class MultiScalarDistributionItem extends ConfigItem {

	private SortModeDist sortMode;
	private xAxisSelection xAxis;

	public MultiScalarDistributionItem(String name) {
		super(name);
		this.sortMode = SortModeDist.distribution;
		this.xAxis = xAxisSelection.x1;
	}

	public MultiScalarDistributionItem(String name, SortModeDist sortMode,
			xAxisSelection xAxis, yAxisSelection yAxis,
			DisplayMode displayMode, GraphVisibility visibility) {
		super(name, displayMode, yAxis, visibility);
		this.sortMode = sortMode;
		this.xAxis = xAxis;
	}

	public MultiScalarDistributionItem(String name, SortModeDist sortMode,
			xAxisSelection xAxis, yAxisSelection yAxis,
			DisplayMode displayMode, GraphVisibility visibility, Color color) {
		super(name, displayMode, yAxis, visibility, color);
		this.sortMode = sortMode;
		this.xAxis = xAxis;
	}

	public SortModeDist getSortMode() {
		return this.sortMode;
	}

	public xAxisSelection getXAxis() {
		return this.xAxis;
	}
}
