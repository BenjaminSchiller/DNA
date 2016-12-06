package dna.visualization.config.components;

import dna.visualization.MainDisplay;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object storing config parameters for the label visualizer.
 * 
 * @author Rwilmes
 * 
 */
public class LabelVisualizerConfig {

	protected String name;

	// x axis
	protected String x1AxisTitle;
	protected String xAxisType;
	protected String xAxisFormat;

	// y axis
	protected String y1AxisTitle;
	protected String y2AxisTitle;

	protected int positionX;
	protected int positionY;
	protected int rowSpan;
	protected int colSpan;

	protected boolean traceModeLtd;

	protected MenuBarConfig menuBarConfig;

	public LabelVisualizerConfig(String name, int positionX, int positionY, int rowSpan, int colSpan,
			String x1AxisTitle, String xAxisType, String xAxisFormat, String y1AxisTitle, String y2AxisTitle,
			boolean traceModeLtd, MenuBarConfig menuBarConfig) {
		this.name = name;
		this.positionX = positionX;
		this.positionY = positionY;
		this.rowSpan = rowSpan;
		this.colSpan = colSpan;

		this.x1AxisTitle = x1AxisTitle;
		this.xAxisType = xAxisType;
		this.xAxisFormat = xAxisFormat;
		this.y1AxisTitle = y1AxisTitle;
		this.y2AxisTitle = y2AxisTitle;

		this.traceModeLtd = traceModeLtd;

		this.menuBarConfig = menuBarConfig;
	}

	public String getName() {
		return name;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public int getColSpan() {
		return colSpan;
	}

	public String getX1AxisTitle() {
		return x1AxisTitle;
	}

	public String getxAxisType() {
		return xAxisType;
	}

	public String getxAxisFormat() {
		return xAxisFormat;
	}

	public String getY1AxisTitle() {
		return y1AxisTitle;
	}

	public String getY2AxisTitle() {
		return y2AxisTitle;
	}

	public boolean isTraceModeLtd() {
		return traceModeLtd;
	}

	public MenuBarConfig getMenuBarConfig() {
		return menuBarConfig;
	}

	public static LabelVisualizerConfig createLabelVisualizerConfigFromJSONObject(JSONObject o) {
		String name = "Label Visualizer 1";
		int positionX = -1;
		int positionY = -1;
		int rowSpan = 1;
		int colSpan = 1;

		// x axis
		String x1AxisTitle = "Timestamp";
		String xAxisType = "date";
		String xAxisFormat = "hh:mm:ss:SS";

		// y axis
		String y1AxisTitle = "y1";
		String y2AxisTitle = "y2";

		boolean traceModeLtd = true;

		MenuBarConfig menuBarConfig = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0].getMenuBarConfig();
		// MenuBarConfig menuBarConfig = new MenuBarConfig(new Dimension(635,
		// 50),
		// new Font("Dialog", Font.PLAIN, 11), Color.BLACK, true, true,
		// true, true, true);
		// "MenuBar": {
		// "Width": 635,
		// "Height": 50,
		// "showIntervalPanel": true,
		// "showXOptionsPanel": true,
		// "showYOptionsPanel": true,
		// "showCoordsPanel": true,
		// "CoordsFont": {
		// "Name": "Dialog",
		// "Style": "PLAIN",
		// "Size": 11,
		// "Color": "BLACK"
		// }
		// },

		return new LabelVisualizerConfig(name, positionX, positionY, rowSpan, colSpan, x1AxisTitle, xAxisType,
				xAxisFormat, y1AxisTitle, y2AxisTitle, traceModeLtd, menuBarConfig);
	}

}
