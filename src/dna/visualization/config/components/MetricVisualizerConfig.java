package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;

import dna.visualization.MainDisplay;
import dna.visualization.config.VisualizerListConfig;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object to configure metric visualizer windows.
 * 
 * @author Rwilmes
 */
public class MetricVisualizerConfig {

	// constructor
	public MetricVisualizerConfig(String name, int traceLength,
			boolean traceModeLtd, boolean paintLinesPoint, int linesPointSize,
			boolean paintFill, int verticalBarSize, int positionX,
			int positionY, int rowSpan, int colSpan, Dimension chartSize,
			double xAxisOffset, String x1AxisTitle, String xAxisType,
			String xAxisFormat, String y1AxisTitle, String y2AxisTitle,
			Dimension legendSize, Dimension legendItemSize,
			Dimension legendItemButtonSize,
			Dimension legendItemButtonPanelSize,
			Dimension legendItemNameLabelSize,
			Dimension legendItemValueLabelSize, Font legendItemValueFont,
			Color legendItemValueFontColor, VisualizerListConfig listConfig,
			MenuBarConfig menuBarConfig) {
		this.name = name;
		this.traceLength = traceLength;
		this.traceModeLtd = traceModeLtd;
		this.paintLinesPoint = paintLinesPoint;
		this.linesPointSize = linesPointSize;
		this.paintFill = paintFill;
		this.verticalBarSize = verticalBarSize;
		this.positionX = positionX;
		this.positionY = positionY;
		this.rowSpan = rowSpan;
		this.colSpan = colSpan;
		this.chartSize = chartSize;
		this.xAxisOffset = xAxisOffset;
		this.x1AxisTitle = x1AxisTitle;
		this.xAxisType = xAxisType;
		this.xAxisFormat = xAxisFormat;
		this.y1AxisTitle = y1AxisTitle;
		this.y2AxisTitle = y2AxisTitle;
		this.legendSize = legendSize;
		this.legendItemSize = legendItemSize;
		this.legendItemButtonSize = legendItemButtonSize;
		this.legendItemButtonPanelSize = legendItemButtonPanelSize;
		this.legendItemNameLabelSize = legendItemNameLabelSize;
		this.legendItemValueLabelSize = legendItemValueLabelSize;
		this.legendItemValueFont = legendItemValueFont;
		this.legendItemValueFontColor = legendItemValueFontColor;
		this.listConfig = listConfig;
		this.menuBarConfig = menuBarConfig;
	}

	// general options
	private String name;
	private int traceLength;
	private boolean traceModeLtd;
	private boolean paintLinesPoint;
	private int linesPointSize;
	private boolean paintFill;
	private int verticalBarSize;

	// position
	private int positionX;
	private int positionY;
	private int rowSpan;
	private int colSpan;

	// chart options
	private Dimension chartSize;
	private double xAxisOffset;

	// x axis
	private String x1AxisTitle;
	private String xAxisType;
	private String xAxisFormat;

	// y axis
	private String y1AxisTitle;
	private String y2AxisTitle;

	// legend options
	private Dimension legendSize;
	private Dimension legendItemSize;
	private Dimension legendItemButtonSize;
	private Dimension legendItemButtonPanelSize;
	private Dimension legendItemNameLabelSize;
	private Dimension legendItemValueLabelSize;
	private Font legendItemValueFont;
	private Color legendItemValueFontColor;
	private VisualizerListConfig listConfig;

	// menu bar options
	private MenuBarConfig menuBarConfig;

	// get methods
	public String getName() {
		return this.name;
	}

	public int getTraceLength() {
		return this.traceLength;
	}

	public boolean isTraceModeLtd() {
		return this.traceModeLtd;
	}

	public boolean isPaintLinesPoint() {
		return this.paintLinesPoint;
	}

	public int getLinesPointSize() {
		return this.linesPointSize;
	}

	public boolean isPaintFill() {
		return this.paintFill;
	}

	public int getVerticalBarSize() {
		return this.verticalBarSize;
	}

	public int getPositionX() {
		return this.positionX;
	}

	public int getPositionY() {
		return this.positionY;
	}

	public int getRowSpan() {
		return this.rowSpan;
	}

	public int getColSpan() {
		return this.colSpan;
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

	public String getxAxisType() {
		return this.xAxisType;
	}

	public String getxAxisFormat() {
		return this.xAxisFormat;
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

	public Dimension getLegendItemSize() {
		return this.legendItemSize;
	}

	public Dimension getLegendItemButtonSize() {
		return this.legendItemButtonSize;
	}

	public Dimension getLegendItemButtonPanelSize() {
		return this.legendItemButtonPanelSize;
	}

	public Dimension getLegendItemNameLabelSize() {
		return this.legendItemNameLabelSize;
	}

	public Dimension getLegendItemValueLabelSize() {
		return this.legendItemValueLabelSize;
	}

	public Font getLegendItemValueFont() {
		return this.legendItemValueFont;
	}

	public Color getLegendItemValueFontColor() {
		return this.legendItemValueFontColor;
	}

	public VisualizerListConfig getListConfig() {
		return this.listConfig;
	}

	public MenuBarConfig getMenuBarConfig() {
		return this.menuBarConfig;
	}

	/** Creates a metric visualizer config object from a given json object. **/
	public static MetricVisualizerConfig createMetricVisualizerConfigFromJSONObject(
			JSONObject o) {
		// init
		String name;
		int traceLength;
		boolean traceModeLtd;
		boolean paintLinesPoint;
		int linesPointSize;
		boolean paintFill;
		int verticalBarSize;
		int positionX = -1;
		int positionY = -1;
		int rowSpan = 1;
		int colSpan = 1;
		Dimension chartSize;
		double xAxisOffset;
		String x1AxisTitle;
		String xAxisType;
		String xAxisFormat;
		String y1AxisTitle;
		String y2AxisTitle;
		Dimension legendSize;
		Dimension legendItemSize;
		Dimension legendItemButtonSize;
		Dimension legendItemButtonPanelSize;
		Dimension legendItemNameLabelSize;
		Dimension legendItemValueLabelSize;
		Font legendItemValueFont;
		Color legendItemValueFontColor;

		// set default values
		if (MainDisplay.DefaultConfig == null) {
			// if the defaultconfig is not set, use this default values
			name = o.getString("Name");
			traceLength = o.getInt("TraceLength");
			traceModeLtd = o.getBoolean("TraceModeLtd");
			JSONObject chart = o.getJSONObject("Chart");
			chartSize = new Dimension(chart.getInt("Width"),
					chart.getInt("Height"));
			x1AxisTitle = chart.getString("xAxisTitle");
			xAxisType = chart.getString("xAxisType");
			xAxisFormat = chart.getString("xAxisFormat");
			xAxisOffset = chart.getDouble("xAxisOffset");
			y1AxisTitle = chart.getString("y1AxisTitle");
			y2AxisTitle = chart.getString("y2AxisTitle");
			paintLinesPoint = chart.getBoolean("PaintLinesPoint");
			linesPointSize = chart.getInt("LinesPointSize");
			paintFill = chart.getBoolean("PaintFill");
			verticalBarSize = chart.getInt("VerticalBarSize");
			JSONObject legend = o.getJSONObject("Legend");
			legendSize = new Dimension(legend.getInt("Width"),
					legend.getInt("Height"));
			JSONObject legendItem = legend.getJSONObject("LegendItem");
			legendItemSize = new Dimension(legendItem.getInt("Width"),
					legendItem.getInt("Height"));
			legendItemButtonSize = new Dimension(
					legendItem.getInt("Button_Width"),
					legendItem.getInt("Button_Height"));
			legendItemButtonPanelSize = new Dimension(
					legendItem.getInt("ButtonPanel_Width"),
					legendItem.getInt("ButtonPanel_Height"));
			legendItemNameLabelSize = new Dimension(
					legendItem.getInt("NameLabel_Width"),
					legendItem.getInt("NameLabel_Height"));
			legendItemValueLabelSize = new Dimension(
					legendItem.getInt("ValueLabel_Width"),
					legendItem.getInt("ValueLabel_Height"));
			JSONObject fontObject = legendItem.getJSONObject("ValueFont");
			String tempName = fontObject.getString("Name");
			String tempStyle = fontObject.getString("Style");
			int tempSize = fontObject.getInt("Size");
			int style;
			switch (tempStyle) {
			case "PLAIN":
				style = Font.PLAIN;
				break;
			case "BOLD":
				style = Font.BOLD;
				break;
			case "ITALIC":
				style = Font.ITALIC;
				break;
			default:
				style = Font.PLAIN;
				break;
			}
			legendItemValueFont = new Font(tempName, style, tempSize);
			legendItemValueFontColor = Color.BLACK;
			try {
				Field field = Color.class.getField(fontObject
						.getString("Color"));
				legendItemValueFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
		} else {
			// use default config values as defaults
			name = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0]
					.getName();
			traceLength = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getTraceLength();
			traceModeLtd = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].isTraceModeLtd();
			paintLinesPoint = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].isPaintLinesPoint();
			linesPointSize = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getLinesPointSize();
			paintFill = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0]
					.isPaintFill();
			verticalBarSize = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getVerticalBarSize();
			chartSize = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0]
					.getChartSize();
			xAxisOffset = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getxAxisOffset();
			x1AxisTitle = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getx1AxisTitle();
			xAxisType = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0]
					.getxAxisType();
			xAxisFormat = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getxAxisFormat();
			y1AxisTitle = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getY1AxisTitle();
			y2AxisTitle = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getY2AxisTitle();
			legendSize = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0]
					.getLegendSize();
			legendItemSize = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getLegendItemSize();
			legendItemButtonSize = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getLegendItemButtonSize();
			legendItemButtonPanelSize = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0]
					.getLegendItemButtonPanelSize();
			legendItemNameLabelSize = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0]
					.getLegendItemNameLabelSize();
			legendItemValueLabelSize = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0]
					.getLegendItemValueLabelSize();
			legendItemValueFont = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getLegendItemValueFont();
			legendItemValueFontColor = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0]
					.getLegendItemValueFontColor();
		}

		// overwrite default values with parsed values
		try {
			name = o.getString("Name");
		} catch (Exception e) {
		}

		try {
			traceLength = o.getInt("TraceLength");
		} catch (Exception e) {
		}

		try {
			traceModeLtd = o.getBoolean("TraceModeLtd");
		} catch (Exception e) {
		}

		try {
			JSONObject positionObject = o.getJSONObject("position");
			try {
				positionX = positionObject.getInt("x");
				positionY = positionObject.getInt("y");
			} catch (Exception e) {
			}

			try {
				rowSpan = positionObject.getInt("rowspawn");
				colSpan = positionObject.getInt("colspan");
			} catch (Exception e) {
			}
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
				case "PaintLinesPoint":
					paintLinesPoint = chart.getBoolean(s);
					break;
				case "LinesPointSize":
					linesPointSize = chart.getInt(s);
					break;
				case "PaintFill":
					paintFill = chart.getBoolean(s);
					break;
				case "VerticalBarSize":
					verticalBarSize = chart.getInt(s);
					break;
				case "xAxisTitle":
					x1AxisTitle = chart.getString(s);
					break;
				case "xAxisType":
					xAxisType = chart.getString(s);
					break;
				case "xAxisFormat":
					xAxisFormat = chart.getString(s);
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
			try {
				JSONObject legendItem = legend.getJSONObject("LegendItem");
				try {
					legendItemSize = new Dimension(legendItem.getInt("Width"),
							legendItem.getInt("Height"));
				} catch (Exception e) {
				}
				try {
					legendItemButtonSize = new Dimension(
							legendItem.getInt("Button_Width"),
							legendItem.getInt("Button_Height"));
				} catch (Exception e) {
				}
				try {
					legendItemButtonPanelSize = new Dimension(
							legendItem.getInt("ButtonPanel_Width"),
							legendItem.getInt("ButtonPanel_Height"));
				} catch (Exception e) {
				}
				try {
					legendItemNameLabelSize = new Dimension(
							legendItem.getInt("NameLabel_Width"),
							legendItem.getInt("NameLabel_Height"));
				} catch (Exception e) {
				}
				try {
					legendItemValueLabelSize = new Dimension(
							legendItem.getInt("ValueLabel_Width"),
							legendItem.getInt("ValueLabel_Height"));
				} catch (Exception e) {
				}
				try {
					JSONObject fontObject = legendItem
							.getJSONObject("ValueFont");

					String tempName = fontObject.getString("Name");
					String tempStyle = fontObject.getString("Style");
					int tempSize = fontObject.getInt("Size");
					int style;
					switch (tempStyle) {
					case "PLAIN":
						style = Font.PLAIN;
						break;
					case "BOLD":
						style = Font.BOLD;
						break;
					case "ITALIC":
						style = Font.ITALIC;
						break;
					default:
						style = Font.PLAIN;
						break;
					}
					legendItemValueFont = new Font(tempName, style, tempSize);
					legendItemValueFontColor = Color.BLACK;
					try {
						Field field = Color.class.getField(fontObject
								.getString("Color"));
						legendItemValueFontColor = (Color) field.get(null);
					} catch (Exception e) {
					}
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		// parse list config
		VisualizerListConfig listConfig = null;
		try {
			listConfig = VisualizerListConfig.createConfigFromJSONObject(o);
		} catch (Exception e) {
		}
		// calculate menu bar config
		MenuBarConfig menuBarConfig = MenuBarConfig
				.createMenuBarConfigFromJSONObject(o.getJSONObject("MenuBar"));

		return new MetricVisualizerConfig(name, traceLength, traceModeLtd,
				paintLinesPoint, linesPointSize, paintFill, verticalBarSize,
				positionX, positionY, rowSpan, colSpan, chartSize, xAxisOffset,
				x1AxisTitle, xAxisType, xAxisFormat, y1AxisTitle, y2AxisTitle,
				legendSize, legendItemSize, legendItemButtonSize,
				legendItemButtonPanelSize, legendItemNameLabelSize,
				legendItemValueLabelSize, legendItemValueFont,
				legendItemValueFontColor, listConfig, menuBarConfig);
	}
}
