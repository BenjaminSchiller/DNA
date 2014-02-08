package dna.visualization.config.components;

import java.awt.Dimension;

import dna.visualization.GuiOptions;
import dna.visualization.config.VisualizerListConfig;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object to configure multicscalar visualizer windows.
 * 
 * @author Rwilmes
 */
public class MultiScalarVisualizerConfig {

	public MultiScalarVisualizerConfig(String name, Dimension chartSize,
			double xAxisOffset, String x1AxisTitle, String x2AxisTitle,
			String y1AxisTitle, String y2AxisTitle, Dimension legendSize,
			VisualizerListConfig listConfig, MenuBarConfig menuBarConfig) {
		this.name = name;
		this.chartSize = chartSize;
		this.xAxisOffset = xAxisOffset;
		this.x1AxisTitle = x1AxisTitle;
		this.x2AxisTitle = x2AxisTitle;
		this.y1AxisTitle = y1AxisTitle;
		this.y2AxisTitle = y2AxisTitle;
		this.legendSize = legendSize;
		this.listConfig = listConfig;
		this.menuBarConfig = menuBarConfig;
	}

	// general options
	private String name;

	// chart options
	private Dimension chartSize;
	private double xAxisOffset;

	// x axis
	private String x1AxisTitle;
	private String x2AxisTitle;

	// y axis
	private String y1AxisTitle;
	private String y2AxisTitle;

	// legend options
	private Dimension legendSize;
	private VisualizerListConfig listConfig;

	// menu bar options
	private MenuBarConfig menuBarConfig;

	// get methods
	public String getName() {
		return this.name;
	}

	public Dimension getChartSize() {
		return this.chartSize;
	}

	public double getxAxisOffset() {
		return this.xAxisOffset;
	}

	public String getx1AxisTitle() {
		return this.x1AxisTitle;
	}

	public String getx2AxisTitle() {
		return this.x2AxisTitle;
	}

	public String getY1AxisTitle() {
		return this.y1AxisTitle;
	}

	public String getY2AxisTitle() {
		return this.y2AxisTitle;
	}

	public Dimension getLegendSize() {
		return this.legendSize;
	}

	public VisualizerListConfig getListConfig() {
		return this.listConfig;
	}

	public MenuBarConfig getMenuBarConfig() {
		return this.menuBarConfig;
	}

	/**
	 * Creates a multi scalar visualizer config object from a given json object.
	 **/
	public static MultiScalarVisualizerConfig createMultiScalarVisualizerConfigFromJSONObject(
			JSONObject o) {
		String name = GuiOptions.multiScalarVisualizerDefaultTitle;
		Dimension chartSize = GuiOptions.visualizerDefaultChartSize;

		double xAxisOffset = GuiOptions.multiScalarVisualizerXAxisOffset;

		String x1AxisTitle = GuiOptions.visualizerDefaultX1AxisTitle;
		String x2AxisTitle = GuiOptions.visualizerDefaultX2AxisTitle;

		String y1AxisTitle = GuiOptions.visualizerDefaultY1AxisTitle;
		String y2AxisTitle = GuiOptions.visualizerDefaultY2AxisTitle;

		Dimension legendSize = GuiOptions.visualizerDefaultLegendSize;

		try {
			name = o.getString("Name");
		} catch (Exception e) {
		}

		try {
			JSONObject chart = o.getJSONObject("Chart");
			try {
				chartSize = new Dimension(chart.getInt("Width"),
						chart.getInt("Height"));
			} catch (Exception e) {
			}

			for (String s : JSONObject.getNames(chart)) {
				switch (s) {
				case "x1AxisTitle":
					x1AxisTitle = chart.getString(s);
					break;
				case "x2AxisTitle":
					x2AxisTitle = chart.getString(s);
					break;
				case "xAxisOffset":
					xAxisOffset = chart.getDouble(s);
					break;
				case "y1AxisTitle":
					y1AxisTitle = chart.getString(s);
					break;
				case "y2AxisTitle":
					y2AxisTitle = chart.getString(s);
					break;
				}
			}
		} catch (Exception e) {
		}

		// legend
		try {
			JSONObject legend = o.getJSONObject("Legend");
			legendSize = new Dimension(legend.getInt("Width"),
					legend.getInt("Height"));
		} catch (Exception e) {
		}

		// parse list config
		VisualizerListConfig listConfig = VisualizerListConfig
				.createConfigFromJSONObject(o);

		// calculate menu bar config
		MenuBarConfig menuBarConfig = MenuBarConfig
				.createMenuBarConfigFromJSONObject(o.getJSONObject("MenuBar"));

		return new MultiScalarVisualizerConfig(name, chartSize, xAxisOffset,
				x1AxisTitle, x2AxisTitle, y1AxisTitle, y2AxisTitle, legendSize,
				listConfig, menuBarConfig);
	}
}
