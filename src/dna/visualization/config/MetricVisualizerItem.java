package dna.visualization.config;

import java.awt.Color;
import java.lang.reflect.Field;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;
import dna.visualization.config.JSON.JSONObject;

/**
 * Specialized ConfigItem object for all values that can be used in the
 * MetricVisualizer.
 * 
 * @author Rwilmes
 */
public class MetricVisualizerItem extends ConfigItem {

	public MetricVisualizerItem(String name) {
		super(name);
	}

	public MetricVisualizerItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility) {
		super(name, displayMode, yAxis, visibility);
	}

	public MetricVisualizerItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, int orderId) {
		super(name, displayMode, yAxis, visibility, orderId);
	}

	public MetricVisualizerItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, Color color) {
		super(name, displayMode, yAxis, visibility, color);
	}

	public MetricVisualizerItem(String name, DisplayMode displayMode,
			yAxisSelection yAxis, GraphVisibility visibility, Color color,
			int orderId) {
		super(name, displayMode, yAxis, visibility, color, orderId);
	}

	/** creates a MetricVisualizerItem from a json object **/
	public static MetricVisualizerItem createMetricVisualizerItemFromJSONObject(
			JSONObject o) {
		String name = o.getString("Name");
		DisplayMode displayMode = metricVisualizerDefaultDisplayMode;
		yAxisSelection yAxis = MetricVisualizerItem.metricVisualizerDefaultYAxisSelection;
		GraphVisibility visibility = MetricVisualizerItem.metricVisualizerDefaultGraphVisibility;
		Color color = null;
		int orderId = -1;

		try {
			if (o.getString("DisplayMode").equals("linespoint"))
				displayMode = DisplayMode.linespoint;
			if (o.getString("DisplayMode").equals("bars"))
				displayMode = DisplayMode.bars;
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
			Field field = Color.class.getField(o.getString("Color"));
			color = (Color) field.get(null);
		} catch (Exception e) {

		}

		try {
			if (o.has("orderId"))
				orderId = o.getInt("orderId");
		} catch (Exception e) {

		}

		if (color != null)
			return new MetricVisualizerItem(name, displayMode, yAxis,
					visibility, color, orderId);
		else
			return new MetricVisualizerItem(name, displayMode, yAxis,
					visibility, orderId);
	}
}
