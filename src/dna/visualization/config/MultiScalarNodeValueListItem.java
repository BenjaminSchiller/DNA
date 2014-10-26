package dna.visualization.config;

import java.awt.Color;
import java.lang.reflect.Field;

import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;
import dna.visualization.config.JSON.JSONObject;

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
		this(name, sortMode, xAxis, yAxis, displayMode, visibility, -1);
	}

	public MultiScalarNodeValueListItem(String name, SortModeNVL sortMode,
			xAxisSelection xAxis, yAxisSelection yAxis,
			DisplayMode displayMode, GraphVisibility visibility, int orderId) {
		super(name, DisplayMode.linespoint, yAxis, visibility, orderId);
		this.sortMode = sortMode;
		this.xAxis = xAxis;
	}

	public MultiScalarNodeValueListItem(String name, SortModeNVL sortMode,
			xAxisSelection xAxis, yAxisSelection yAxis,
			DisplayMode displayMode, GraphVisibility visibility, Color color) {
		this(name, sortMode, xAxis, yAxis, displayMode, visibility, color, -1);
	}

	public MultiScalarNodeValueListItem(String name, SortModeNVL sortMode,
			xAxisSelection xAxis, yAxisSelection yAxis,
			DisplayMode displayMode, GraphVisibility visibility, Color color,
			int orderId) {
		super(name, DisplayMode.linespoint, yAxis, visibility, color, orderId);
		this.sortMode = sortMode;
		this.xAxis = xAxis;
	}

	public SortModeNVL getSortMode() {
		return this.sortMode;
	}

	public xAxisSelection getXAxis() {
		return this.xAxis;
	}

	/** creates a MultiScalarNodeValueListItem from a json object **/
	public static MultiScalarNodeValueListItem createMultiScalarNodeValueListItemFromJSONObject(
			JSONObject o) {
		String name = o.getString("Name");
		SortModeNVL sortMode = MultiScalarNodeValueListItem.multiScalarVisualizerDefaultNodeValueListSortMode;
		xAxisSelection xAxis = MultiScalarNodeValueListItem.multiScalarVisualizerDefaultNVLXAxisSelection;
		yAxisSelection yAxis = MultiScalarNodeValueListItem.multiScalarVisualizerDefaultNVLYAxisSelection;
		DisplayMode displayMode = MultiScalarNodeValueListItem.multiScalarVisualizerDefaultNodeValueListDisplayMode;
		GraphVisibility visibility = MultiScalarNodeValueListItem.multiScalarVisualizerDefaultGraphVisibility;
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
			if (o.getString("SortMode").equals("ascending"))
				sortMode = SortModeNVL.ascending;
			if (o.getString("SortMode").equals("descending"))
				sortMode = SortModeNVL.descending;
			if (o.getString("SortMode").equals("index"))
				sortMode = SortModeNVL.index;
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

		try {
			if (o.has("orderId"))
				orderId = o.getInt("orderId");
		} catch (Exception e) {

		}

		if (color != null)
			return new MultiScalarNodeValueListItem(name, sortMode, xAxis,
					yAxis, displayMode, visibility, color, orderId);
		else
			return new MultiScalarNodeValueListItem(name, sortMode, xAxis,
					yAxis, displayMode, visibility, orderId);
	}
}
