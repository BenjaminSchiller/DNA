package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;

import dna.visualization.GuiOptions;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object to configure log display windows.
 * 
 * @author Rwilmes
 */
public class LogDisplayConfig {

	// constructor
	public LogDisplayConfig(String name, String dir, int positionX,
			int positionY, long updateInterval, Font logFont,
			Color logFontColor, Dimension textFieldSize, boolean showInfo,
			boolean showWarning, boolean showError, boolean showDebug) {
		this.name = name;
		this.dir = dir;
		this.positionX = positionX;
		this.positionY = positionY;
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
		String name = GuiOptions.logDefaultTitle;
		String dir = GuiOptions.defaultLogDir;
		int positionX = -1;
		int positionY = -1;
		long updateInterval = GuiOptions.logDefaultUpdateInterval;
		Font logFont = GuiOptions.defaultFont;
		Color logFontColor = GuiOptions.defaultFontColor;
		Dimension textFieldSize = GuiOptions.logDefaultTextFieldSize;

		boolean showInfo = GuiOptions.logDefaultShowInfo;
		boolean showWarning = GuiOptions.logDefaultShowWarning;
		boolean showError = GuiOptions.logDefaultShowError;
		boolean showDebug = GuiOptions.logDefaultShowDebug;

		try {
			name = o.getString("Name");
		} catch (Exception e) {
		}

		try {
			dir = o.getString("Dir");
		} catch (Exception e) {
		}

		try {
			positionX = o.getInt("PositionX");
			positionY = o.getInt("PositionY");
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

		return new LogDisplayConfig(name, dir, positionX, positionY,
				updateInterval, logFont, logFontColor, textFieldSize, showInfo,
				showWarning, showError, showDebug);
	}

}
