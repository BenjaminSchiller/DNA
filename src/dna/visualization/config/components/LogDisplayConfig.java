package dna.visualization.config.components;

import java.awt.Dimension;

import dna.visualization.GuiOptions;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object to configure log display windows.
 * 
 * @author Rwilmes
 */
public class LogDisplayConfig {

	// constructor
	public LogDisplayConfig(String name, String dir, long updateInterval,
			Dimension textFieldSize, boolean showInfo, boolean showWarning,
			boolean showError, boolean showDebug) {
		this.name = name;
		this.dir = dir;
		this.updateInterval = updateInterval;
		this.textFieldSize = textFieldSize;
		this.showInfo = showInfo;
		this.showWarning = showWarning;
		this.showError = showError;
		this.showDebug = showDebug;
	}

	// general options
	private String name;
	private String dir;
	private long updateInterval;

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

	public long getUpdateInterval() {
		return this.updateInterval;
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
		long updateInterval = GuiOptions.logDefaultUpdateInterval;
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
			updateInterval = o.getLong("UpdateInterval");
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

		return new LogDisplayConfig(name, dir, updateInterval, textFieldSize,
				showInfo, showWarning, showError, showDebug);
	}

}
