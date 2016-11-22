package dna.visualization.config.components;

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
	private String x1AxisTitle;
	private String xAxisType;
	private String xAxisFormat;

	// y axis
	private String y1AxisTitle;
	private String y2AxisTitle;

	protected int positionX;
	protected int positionY;
	protected int rowSpan;
	protected int colSpan;

	protected boolean traceModeLtd;

	public LabelVisualizerConfig(String name, int positionX, int positionY,
			int rowSpan, int colSpan, String x1AxisTitle, String xAxisType,
			String xAxisFormat, String y1AxisTitle, String y2AxisTitle,
			boolean traceModeLtd) {
		System.out.println("init lv config");
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

	public static LabelVisualizerConfig createLabelVisualizerConfigFromJSONObject(
			JSONObject o) {
		String name = "Label Visualizer 1";
		int positionX = -1;
		int positionY = -1;
		int rowSpan = 1;
		int colSpan = 1;

		// x axis
		String x1AxisTitle = "x";
		String xAxisType = "date";
		String xAxisFormat = "s";

		// y axis
		String y1AxisTitle = "y1";
		String y2AxisTitle = "y2";

		boolean traceModeLtd = true;

		return new LabelVisualizerConfig(name, positionX, positionY, rowSpan,
				colSpan, x1AxisTitle, xAxisType, xAxisFormat, y1AxisTitle,
				y2AxisTitle, traceModeLtd);
	}

}
