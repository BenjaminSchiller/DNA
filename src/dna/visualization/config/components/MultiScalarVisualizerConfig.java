package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;

import dna.visualization.MainDisplay;
import dna.visualization.config.VisualizerListConfig;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object to configure multicscalar visualizer windows.
 * 
 * @author Rwilmes
 */
public class MultiScalarVisualizerConfig {

	public MultiScalarVisualizerConfig(String name, boolean paintLinesPoint,
			int linesPointSize, boolean paintFill, int verticalBarSize,
			int positionX, int positionY, int rowSpan, int colSpan,
			Dimension chartSize, double xAxisOffset, String x1AxisTitle,
			String x2AxisTitle, String y1AxisTitle, String y2AxisTitle,
			Dimension legendSize, Dimension legendItemSize,
			Dimension legendItemButtonSize,
			Dimension legendItemButtonPanelSize,
			Dimension legendItemNameLabelSize,
			Dimension legendItemValueLabelSize, Font legendItemValueFont,
			Color legendItemValueFontColor, VisualizerListConfig listConfig,
			MenuBarConfig menuBarConfig) {
		this.name = name;
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
		this.x2AxisTitle = x2AxisTitle;
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
	private String x2AxisTitle;

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

	/**
	 * Creates a multi scalar visualizer config object from a given json object.
	 **/
	public static MultiScalarVisualizerConfig createMultiScalarVisualizerConfigFromJSONObject(
			JSONObject o) {
		// init
		String name;
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
		String x2AxisTitle;
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
			JSONObject chart = o.getJSONObject("Chart");
			chartSize = new Dimension(chart.getInt("Width"),
					chart.getInt("Height"));
			x1AxisTitle = chart.getString("x1AxisTitle");
			x2AxisTitle = chart.getString("x2AxisTitle");
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
			name = MainDisplay.DefaultConfig.getMultiScalarVisualizerConfigs()[0]
					.getName();
			paintLinesPoint = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].isPaintLinesPoint();
			linesPointSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getLinesPointSize();
			paintFill = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].isPaintFill();
			verticalBarSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getVerticalBarSize();
			chartSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getChartSize();
			xAxisOffset = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getxAxisOffset();
			x1AxisTitle = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getx1AxisTitle();
			x2AxisTitle = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getx2AxisTitle();
			y1AxisTitle = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getY1AxisTitle();
			y2AxisTitle = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getY2AxisTitle();
			legendSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getLegendSize();
			legendItemSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0].getLegendItemSize();
			legendItemButtonSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0]
					.getLegendItemButtonSize();
			legendItemButtonPanelSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0]
					.getLegendItemButtonPanelSize();
			legendItemNameLabelSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0]
					.getLegendItemNameLabelSize();
			legendItemValueLabelSize = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0]
					.getLegendItemValueLabelSize();
			legendItemValueFont = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0]
					.getLegendItemValueFont();
			legendItemValueFontColor = MainDisplay.DefaultConfig
					.getMultiScalarVisualizerConfigs()[0]
					.getLegendItemValueFontColor();
		}

		// overwrite default values with parsed values
		try {
			name = o.getString("Name");
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

		return new MultiScalarVisualizerConfig(name, paintLinesPoint,
				linesPointSize, paintFill, verticalBarSize, positionX,
				positionY, rowSpan, colSpan, chartSize, xAxisOffset,
				x1AxisTitle, x2AxisTitle, y1AxisTitle, y2AxisTitle, legendSize,
				legendItemSize, legendItemButtonSize,
				legendItemButtonPanelSize, legendItemNameLabelSize,
				legendItemValueLabelSize, legendItemValueFont,
				legendItemValueFontColor, listConfig, menuBarConfig);
	}
}
