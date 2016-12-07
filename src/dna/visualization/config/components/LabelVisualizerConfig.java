package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;

import dna.visualization.MainDisplay;
import dna.visualization.config.JSON.JSONArray;
import dna.visualization.config.JSON.JSONObject;

/**
 * A config object for label visualizers.
 * 
 * @author Rwilmes
 *
 */
public class LabelVisualizerConfig {

	/** Enumeration for addition policies. **/
	public enum LabelAdditionPolicy {
		AUTOMATIC_ADDITION_ALL, AUTOMATIC_ADDITION_LIST, MANUAL
	}

	public LabelVisualizerConfig(String name, int traceLength, boolean traceModeLtd, double barThickness,
			LabelAdditionPolicy additionPolicy, String[] additionList, int positionX, int positionY, int rowSpan,
			int colSpan, Dimension chartSize, String x1AxisTitle, String xAxisType, String xAxisFormat,
			String y1AxisTitle, Dimension legendSize, Dimension legendItemSize, Dimension legendItemButtonSize,
			Dimension legendItemButtonPanelSize, Dimension legendItemNameLabelSize, Dimension legendItemValueLabelSize,
			Font legendItemValueFont, Color legendItemValueFontColor, MenuBarConfig menuBarConfig) {
		this.name = name;
		this.traceLength = traceLength;
		this.traceModeLtd = traceModeLtd;
		this.barThickness = barThickness;
		this.additionPolicy = additionPolicy;
		this.additionList = additionList;
		this.positionX = positionX;
		this.positionY = positionY;
		this.rowSpan = rowSpan;
		this.colSpan = colSpan;
		this.chartSize = chartSize;
		this.x1AxisTitle = x1AxisTitle;
		this.xAxisType = xAxisType;
		this.xAxisFormat = xAxisFormat;
		this.y1AxisTitle = y1AxisTitle;
		this.legendSize = legendSize;
		this.legendItemSize = legendItemSize;
		this.legendItemButtonSize = legendItemButtonSize;
		this.legendItemButtonPanelSize = legendItemButtonPanelSize;
		this.legendItemNameLabelSize = legendItemNameLabelSize;
		this.legendItemValueLabelSize = legendItemValueLabelSize;
		this.legendItemValueFont = legendItemValueFont;
		this.legendItemValueFontColor = legendItemValueFontColor;
		this.menuBarConfig = menuBarConfig;
	}

	// general options
	private String name;
	private int traceLength;
	private boolean traceModeLtd;
	private double barThickness;

	private LabelAdditionPolicy additionPolicy;
	private String[] additionList;

	// position
	private int positionX;
	private int positionY;
	private int rowSpan;
	private int colSpan;

	// chart options
	private Dimension chartSize;

	// x axis
	private String x1AxisTitle;
	private String xAxisType;
	private String xAxisFormat;

	// y axis
	private String y1AxisTitle;

	// legend options
	private Dimension legendSize;
	private Dimension legendItemSize;
	private Dimension legendItemButtonSize;
	private Dimension legendItemButtonPanelSize;
	private Dimension legendItemNameLabelSize;
	private Dimension legendItemValueLabelSize;
	private Font legendItemValueFont;
	private Color legendItemValueFontColor;

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

	public double getBarThickness() {
		return this.barThickness;
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

	public String getX1AxisTitle() {
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

	public MenuBarConfig getMenuBarConfig() {
		return this.menuBarConfig;
	}

	public LabelAdditionPolicy getAdditionPolicy() {
		return additionPolicy;
	}

	public String[] getAdditionList() {
		return additionList;
	}

	/** Creates a Label visualizer config object from a given json object. **/
	public static LabelVisualizerConfig createLabelVisualizerConfigFromJSONObject(JSONObject o) {
		// init
		String name;
		int traceLength;
		boolean traceModeLtd;
		double barThickness = 0.5;
		LabelAdditionPolicy additionPolicy;
		String[] additionList;

		int positionX = -1;
		int positionY = -1;
		int rowSpan = 1;
		int colSpan = 1;
		Dimension chartSize;
		String x1AxisTitle;
		String xAxisType;
		String xAxisFormat;
		String y1AxisTitle;
		Dimension legendSize;
		Dimension legendItemSize;
		Dimension legendItemButtonSize;
		Dimension legendItemButtonPanelSize;
		Dimension legendItemNameLabelSize;
		Dimension legendItemValueLabelSize;
		Font legendItemValueFont;
		Color legendItemValueFontColor;
		MenuBarConfig menuBarConfig;

		// set default values
		if (MainDisplay.DefaultConfig == null) {
			// if the defaultconfig is not set, use this default values
			name = o.getString("Name");
			traceLength = o.getInt("TraceLength");
			traceModeLtd = o.getBoolean("TraceModeLtd");
			JSONObject chart = o.getJSONObject("Chart");
			chartSize = new Dimension(chart.getInt("Width"), chart.getInt("Height"));
			x1AxisTitle = chart.getString("xAxisTitle");
			xAxisType = chart.getString("xAxisType");
			xAxisFormat = chart.getString("xAxisFormat");
			y1AxisTitle = chart.getString("y1AxisTitle");
			barThickness = chart.getDouble("BarThickness");

			String policyString = o.getString("AdditionPolicy");
			switch (policyString) {
			case "all":
				additionPolicy = LabelAdditionPolicy.AUTOMATIC_ADDITION_ALL;
				break;
			case "list":
				additionPolicy = LabelAdditionPolicy.AUTOMATIC_ADDITION_LIST;
				break;
			default:
				additionPolicy = LabelAdditionPolicy.MANUAL;
				break;
			}

			JSONArray additionListArray = o.getJSONArray("AdditionList");
			additionList = new String[additionListArray.length()];
			for (int i = 0; i < additionListArray.length(); i++) {
				additionList[i] = additionListArray.getString(i);
			}

			JSONObject legend = o.getJSONObject("Legend");
			legendSize = new Dimension(legend.getInt("Width"), legend.getInt("Height"));
			JSONObject legendItem = legend.getJSONObject("LegendItem");
			legendItemSize = new Dimension(legendItem.getInt("Width"), legendItem.getInt("Height"));
			legendItemButtonSize = new Dimension(legendItem.getInt("Button_Width"), legendItem.getInt("Button_Height"));
			legendItemButtonPanelSize = new Dimension(legendItem.getInt("ButtonPanel_Width"),
					legendItem.getInt("ButtonPanel_Height"));
			legendItemNameLabelSize = new Dimension(legendItem.getInt("NameLabel_Width"),
					legendItem.getInt("NameLabel_Height"));
			legendItemValueLabelSize = new Dimension(legendItem.getInt("ValueLabel_Width"),
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
				Field field = Color.class.getField(fontObject.getString("Color"));
				legendItemValueFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
			menuBarConfig = MenuBarConfig.createMenuBarConfigFromJSONObject(o.getJSONObject("MenuBar"));
		} else {
			// use default config values as defaults
			LabelVisualizerConfig defaultConfig = MainDisplay.DefaultConfig.getLabelVisualizerConfigs()[0];
			name = defaultConfig.getName();
			traceLength = defaultConfig.getTraceLength();
			traceModeLtd = defaultConfig.isTraceModeLtd();
			barThickness = defaultConfig.getBarThickness();
			additionPolicy = defaultConfig.getAdditionPolicy();
			additionList = defaultConfig.getAdditionList();
			chartSize = defaultConfig.getChartSize();
			x1AxisTitle = defaultConfig.getX1AxisTitle();
			xAxisType = defaultConfig.getxAxisType();
			xAxisFormat = defaultConfig.getxAxisFormat();
			y1AxisTitle = defaultConfig.getY1AxisTitle();
			legendSize = defaultConfig.getLegendSize();
			legendItemSize = defaultConfig.getLegendItemSize();
			legendItemButtonSize = defaultConfig.getLegendItemButtonSize();
			legendItemButtonPanelSize = defaultConfig.getLegendItemButtonPanelSize();
			legendItemNameLabelSize = defaultConfig.getLegendItemNameLabelSize();
			legendItemValueLabelSize = defaultConfig.getLegendItemValueLabelSize();
			legendItemValueFont = defaultConfig.getLegendItemValueFont();
			legendItemValueFontColor = defaultConfig.getLegendItemValueFontColor();
			menuBarConfig = defaultConfig.getMenuBarConfig();
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
			String policyString = o.getString("AdditionPolicy");

			switch (policyString) {
			case "all":
				additionPolicy = LabelAdditionPolicy.AUTOMATIC_ADDITION_ALL;
				break;
			case "list":
				additionPolicy = LabelAdditionPolicy.AUTOMATIC_ADDITION_LIST;
				break;
			default:
				additionPolicy = LabelAdditionPolicy.MANUAL;
				break;
			}
		} catch (Exception e) {
		}

		try {
			JSONArray additionListArray = o.getJSONArray("AdditionList");
			additionList = new String[additionListArray.length()];
			for (int i = 0; i < additionListArray.length(); i++) {
				additionList[i] = additionListArray.getString(i);
			}
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
				rowSpan = positionObject.getInt("rowspan");
				colSpan = positionObject.getInt("colspan");
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		try {
			JSONObject chart = o.getJSONObject("Chart");
			try {
				chartSize = new Dimension(chart.getInt("Width"), chart.getInt("Height"));
			} catch (Exception e) {
			}

			for (String s : JSONObject.getNames(chart)) {
				switch (s) {
				case "BarThickness":
					barThickness = chart.getDouble(s);
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
				case "y1AxisTitle":
					y1AxisTitle = chart.getString(s);
					break;
				}
			}
		} catch (Exception e) {
		}

		// legend
		try {
			JSONObject legend = o.getJSONObject("Legend");
			legendSize = new Dimension(legend.getInt("Width"), legend.getInt("Height"));
			try {
				JSONObject legendItem = legend.getJSONObject("LegendItem");
				try {
					legendItemSize = new Dimension(legendItem.getInt("Width"), legendItem.getInt("Height"));
				} catch (Exception e) {
				}
				try {
					legendItemButtonSize = new Dimension(legendItem.getInt("Button_Width"),
							legendItem.getInt("Button_Height"));
				} catch (Exception e) {
				}
				try {
					legendItemButtonPanelSize = new Dimension(legendItem.getInt("ButtonPanel_Width"),
							legendItem.getInt("ButtonPanel_Height"));
				} catch (Exception e) {
				}
				try {
					legendItemNameLabelSize = new Dimension(legendItem.getInt("NameLabel_Width"),
							legendItem.getInt("NameLabel_Height"));
				} catch (Exception e) {
				}
				try {
					legendItemValueLabelSize = new Dimension(legendItem.getInt("ValueLabel_Width"),
							legendItem.getInt("ValueLabel_Height"));
				} catch (Exception e) {
				}
				try {
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
						Field field = Color.class.getField(fontObject.getString("Color"));
						legendItemValueFontColor = (Color) field.get(null);
					} catch (Exception e) {
					}
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		// parse menu bar config
		try {
			menuBarConfig = MenuBarConfig.createMenuBarConfigFromJSONObject(o.getJSONObject("MenuBar"));
		} catch (Exception e) {
		}

		// craft metric visualizer config
		return new LabelVisualizerConfig(name, traceLength, traceModeLtd, barThickness, additionPolicy, additionList,
				positionX, positionY, rowSpan, colSpan, chartSize, x1AxisTitle, xAxisType, xAxisFormat, y1AxisTitle,
				legendSize, legendItemSize, legendItemButtonSize, legendItemButtonPanelSize, legendItemNameLabelSize,
				legendItemValueLabelSize, legendItemValueFont, legendItemValueFontColor, menuBarConfig);
	}
}
