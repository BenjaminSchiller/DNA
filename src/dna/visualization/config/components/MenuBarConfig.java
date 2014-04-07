package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;

import dna.visualization.MainDisplay;
import dna.visualization.config.JSON.JSONObject;

public class MenuBarConfig {

	// constructor
	public MenuBarConfig(Dimension size, Font coordsFont,
			Color coordsFontColor, boolean addCoordsPanel,
			boolean addIntervalPanel, boolean addXOptionsPanel,
			boolean addYOptionsPanel) {
		this.size = size;
		this.coordsFont = coordsFont;
		this.coordsFontColor = coordsFontColor;
		this.addCoordsPanel = addCoordsPanel;
		this.addIntervalPanel = addIntervalPanel;
		this.addXOptionsPanel = addXOptionsPanel;
		this.addYOptionsPanel = addYOptionsPanel;
	}

	// variables
	private Dimension size;
	private Font coordsFont;
	private Color coordsFontColor;
	private boolean addCoordsPanel;
	private boolean addIntervalPanel;
	private boolean addXOptionsPanel;
	private boolean addYOptionsPanel;

	// get methods
	public Dimension getSize() {
		return this.size;
	}

	public Font getCoordsFont() {
		return this.coordsFont;
	}

	public Color getCoordsFontColor() {
		return this.coordsFontColor;
	}

	public boolean isAddCoordsPanel() {
		return this.addCoordsPanel;
	}

	public boolean isAddIntervalPanel() {
		return this.addIntervalPanel;
	}

	public boolean isAddXOptionsPanel() {
		return this.addXOptionsPanel;
	}

	public boolean isAddYOptionsPanel() {
		return this.addYOptionsPanel;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	/** creates a menubar config object from a given json object **/
	public static MenuBarConfig createMenuBarConfigFromJSONObject(JSONObject o) {
		// init
		Dimension size;
		Font coordsFont;
		Color coordsFontColor;
		boolean addCoordsPanel;
		boolean addIntervalPanel;
		boolean addXOptionsPanel;
		boolean addYOptionsPanel;

		// set default values
		if (MainDisplay.DefaultConfig == null) {
			// if the defaultconfig is not set, read default values
			size = new Dimension(o.getInt("Width"), o.getInt("Height"));
			addCoordsPanel = o.getBoolean("showCoordsPanel");
			addIntervalPanel = o.getBoolean("showIntervalPanel");
			addXOptionsPanel = o.getBoolean("showXOptionsPanel");
			addYOptionsPanel = o.getBoolean("showYOptionsPanel");
			JSONObject fontObject = o.getJSONObject("CoordsFont");
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
			coordsFont = new Font(tempName, style, tempSize);
			coordsFontColor = Color.BLACK;
			try {
				Field field = Color.class.getField(fontObject
						.getString("Color"));
				coordsFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
			return new MenuBarConfig(size, coordsFont, coordsFontColor,
					addCoordsPanel, addIntervalPanel, addXOptionsPanel,
					addYOptionsPanel);
		} else {
			// use default config values as defaults
			size = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0]
					.getMenuBarConfig().getSize();
			addCoordsPanel = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getMenuBarConfig()
					.isAddCoordsPanel();
			addIntervalPanel = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getMenuBarConfig()
					.isAddIntervalPanel();
			addXOptionsPanel = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getMenuBarConfig()
					.isAddXOptionsPanel();
			addYOptionsPanel = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getMenuBarConfig()
					.isAddYOptionsPanel();
			coordsFont = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0]
					.getMenuBarConfig().getCoordsFont();
			coordsFontColor = MainDisplay.DefaultConfig
					.getMetricVisualizerConfigs()[0].getMenuBarConfig()
					.getCoordsFontColor();
		}

		// overwrite default values with parsed values
		try {
			size = new Dimension(o.getInt("Width"), o.getInt("Height"));
		} catch (Exception e) {
		}

		try {
			addCoordsPanel = o.getBoolean("showCoordsPanel");
		} catch (Exception e) {
		}

		try {
			addIntervalPanel = o.getBoolean("showIntervalPanel");
		} catch (Exception e) {
		}

		try {
			addXOptionsPanel = o.getBoolean("showXOptionsPanel");
		} catch (Exception e) {
		}

		try {
			addYOptionsPanel = o.getBoolean("showYOptionsPanel");
		} catch (Exception e) {
		}

		try {
			JSONObject fontObject = o.getJSONObject("CoordsFont");
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
			coordsFont = new Font(tempName, style, tempSize);
			coordsFontColor = Color.BLACK;
			try {
				Field field = Color.class.getField(fontObject
						.getString("Color"));
				coordsFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		return new MenuBarConfig(size, coordsFont, coordsFontColor,
				addCoordsPanel, addIntervalPanel, addXOptionsPanel,
				addYOptionsPanel);
	}
}
