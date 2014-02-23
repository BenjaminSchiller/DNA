package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;

import dna.visualization.MainDisplay;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object to configure log display windows.
 * 
 * @author Rwilmes
 */
public class LogDisplayConfig {

	// constructor
	public LogDisplayConfig(String name, String dir, int positionX,
			int positionY, int rowSpan, int colSpan, long updateInterval,
			Font logFont, Color logFontColor, Dimension textFieldSize,
			boolean showInfo, boolean showWarning, boolean showError,
			boolean showDebug) {
		this.name = name;
		this.dir = dir;
		this.positionX = positionX;
		this.positionY = positionY;
		this.rowSpan = rowSpan;
		this.colSpan = colSpan;
		this.updateInterval = updateInterval;
		this.logFont = logFont;
		this.logFontColor = logFontColor;
		this.textFieldSize = textFieldSize;
		this.showInfo = showInfo;
		this.showWarning = showWarning;
		this.showError = showError;
		this.showDebug = showDebug;
	}

	// general options
	private String name;
	private String dir;
	private int positionX;
	private int positionY;
	private int rowSpan;
	private int colSpan;
	private long updateInterval;
	private Font logFont;
	private Color logFontColor;

	// sizes
	private Dimension textFieldSize;

	// flags
	private boolean showInfo;
	private boolean showWarning;
	private boolean showError;
	private boolean showDebug;

	// get methods
	public String getName() {
		return this.name;
	}

	public String getDir() {
		return this.dir;
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

	public long getUpdateInterval() {
		return this.updateInterval;
	}

	public Font getLogFont() {
		return this.logFont;
	}

	public Color getLogFontColor() {
		return this.logFontColor;
	}

	public Dimension getTextFieldSize() {
		return this.textFieldSize;
	}

	public boolean isInfoShown() {
		return this.showInfo;
	}

	public boolean isWarningShown() {
		return this.showWarning;
	}

	public boolean isErrorShown() {
		return this.showError;
	}

	public boolean isDebugShown() {
		return this.showDebug;
	}

	/** creates a logdisplay config object from a given json object **/
	public static LogDisplayConfig createLogDisplayConfigFromJSONObject(
			JSONObject o) {
		// init
		String name;
		String dir;
		long updateInterval;
		Font logFont;
		Color logFontColor;
		Dimension textFieldSize;

		int positionX = -1;
		int positionY = -1;
		int rowSpan = 1;
		int colSpan = 1;

		boolean showInfo;
		boolean showWarning;
		boolean showError;
		boolean showDebug;
		// set default values
		if (MainDisplay.DefaultConfig == null) {
			// if the defaultconfig is not set, use this default values
			name = o.getString("Name");
			dir = o.getString("Dir");
			updateInterval = o.getLong("UpdateInterval");
			JSONObject logFontObject = o.getJSONObject("LogFont");
			String tempName = logFontObject.getString("Name");
			String tempStyle = logFontObject.getString("Style");
			int tempSize = logFontObject.getInt("Size");
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
			logFont = new Font(tempName, style, tempSize);
			logFontColor = Color.BLACK;
			try {
				Field field = Color.class.getField(logFontObject
						.getString("Color"));
				logFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
			textFieldSize = new Dimension(o.getInt("textAreaWidth"),
					o.getInt("textAreaHeight"));
			JSONObject defaults = o.getJSONObject("default");
			showInfo = defaults.getBoolean("showInfo");
			showWarning = defaults.getBoolean("showWarning");
			showError = defaults.getBoolean("showError");
			showDebug = defaults.getBoolean("showDebug");
		} else {
			// use default config values as defaults
			name = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.getName();
			dir = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0].getDir();
			updateInterval = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.getUpdateInterval();
			logFont = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.getLogFont();
			logFontColor = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.getLogFontColor();
			textFieldSize = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.getTextFieldSize();
			showInfo = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.isInfoShown();
			showWarning = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.isWarningShown();
			showError = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.isErrorShown();
			showDebug = MainDisplay.DefaultConfig.getLogDisplayConfigs()[0]
					.isDebugShown();
		}

		try {
			name = o.getString("Name");
		} catch (Exception e) {
		}

		try {
			dir = o.getString("Dir");
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
			updateInterval = o.getLong("UpdateInterval");
		} catch (Exception e) {
		}

		try {
			JSONObject logFontObject = o.getJSONObject("LogFont");
			try {
				String tempName = logFontObject.getString("Name");
				String tempStyle = logFontObject.getString("Style");
				int tempSize = logFontObject.getInt("Size");
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
				logFont = new Font(tempName, style, tempSize);
			} catch (Exception e) {
			}
			try {
				Field field = Color.class.getField(logFontObject
						.getString("Color"));
				logFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		try {
			textFieldSize = new Dimension(o.getInt("textAreaWidth"),
					o.getInt("textAreaHeight"));
		} catch (Exception e) {
		}

		try {
			JSONObject defaults = o.getJSONObject("default");

			for (String s : JSONObject.getNames(defaults)) {
				switch (s) {
				case "showInfo":
					showInfo = defaults.getBoolean(s);
					break;
				case "showWarning":
					showWarning = defaults.getBoolean(s);
					break;
				case "showError":
					showError = defaults.getBoolean(s);
					break;
				case "showDebug":
					showDebug = defaults.getBoolean(s);
					break;
				}
			}
		} catch (Exception e) {
		}

		return new LogDisplayConfig(name, dir, positionX, positionY, rowSpan,
				colSpan, updateInterval, logFont, logFontColor, textFieldSize,
				showInfo, showWarning, showError, showDebug);
	}
}
