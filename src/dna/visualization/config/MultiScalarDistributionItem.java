package dna.visualization.config;

import java.awt.Color;
import java.lang.reflect.Field;

import dna.visualization.GuiOptions;
import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;
import dna.visualization.config.JSON.JSONObject;

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

	/** creates a MultiScalarDistributionItem from a json object **/
	public static MultiScalarDistributionItem createMultiScalarDistributionItemFromJSONObject(
			JSONObject o) {
		String name = o.getString("Name");
		SortModeDist sortMode = GuiOptions.multiScalarVisualizerDefaultDistributionSortMode;
		xAxisSelection xAxis = GuiOptions.multiScalarVisualizerDefaultDistXAxisSelection;
		yAxisSelection yAxis = GuiOptions.multiScalarVisualizerDefaultDistYAxisSelection;
		DisplayMode displayMode = GuiOptions.multiScalarVisualizerDefaultDistributionDisplayMode;
		GraphVisibility visibility = GuiOptions.multiScalarVisualizerDefaultGraphVisibility;
		Color color = null;

		try {
			if (o.getString("DisplayMode").equals("linespoint"))
				displayMode = DisplayMode.linespoint;
			if (o.getString("DisplayMode").equals("bars"))
				displayMode = DisplayMode.bars;
		} catch (Exception e) {
		}
		try {
			if (o.getString("SortMode").equals("distribution"))
				sortMode = SortModeDist.distribution;
			if (o.getString("SortMode").equals("cdf"))
				sortMode = SortModeDist.cdf;
		} catch (Exception e) {
		}
		try {
			if (o.getString("xAxis").equals("x1"))
				xAxis = xAxisSelection.x1;
			if (o.getString("xAxis").equals("x2"))
				xAxis = xAxisSelection.x2;
		} catch (Exception e) {
		}
		try {
			if (o.getString("yAxis").equals("y1"))
				yAxis = yAxisSelection.y1;
			if (o.getString("yAxis").equals("y2"))
				yAxis = yAxisSelection.y2;
		} catch (Exception e) {
		}
		try {
			if (o.getBoolean("visible"))
				visibility = GraphVisibility.shown;
			if (!o.getBoolean("visible"))
				visibility = GraphVisibility.hidden;
		} catch (Exception e) {
		}
		try {
			Field field = Color.class.getField("CYAN");
			color = (Color) field.get(null);
		} catch (Exception e) {
		}

		if (color != null)
			return new MultiScalarDistributionItem(name, sortMode, xAxis,
					yAxis, displayMode, visibility, color);
		else
			return new MultiScalarDistributionItem(name, sortMode, xAxis,
					yAxis, displayMode, visibility);
	}
}
