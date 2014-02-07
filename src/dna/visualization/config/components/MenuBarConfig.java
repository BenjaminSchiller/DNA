package dna.visualization.config.components;

import java.awt.Dimension;

import dna.visualization.GuiOptions;
import dna.visualization.config.JSON.JSONObject;

public class MenuBarConfig {

	// constructor
	public MenuBarConfig(Dimension size, boolean addCoordsPanel,
			boolean addIntervalPanel, boolean addXOptionsPanel,
			boolean addYOptionsPanel) {
		this.size = size;
		this.addCoordsPanel = addCoordsPanel;
		this.addIntervalPanel = addIntervalPanel;
		this.addXOptionsPanel = addXOptionsPanel;
		this.addYOptionsPanel = addYOptionsPanel;
	}

	// variables
	private Dimension size;
	private boolean addCoordsPanel;
	private boolean addIntervalPanel;
	private boolean addXOptionsPanel;
	private boolean addYOptionsPanel;

	// get methods
	public Dimension getSize() {
		return size;
	}

	public boolean isAddCoordsPanel() {
		return addCoordsPanel;
	}

	public boolean isAddIntervalPanel() {
		return addIntervalPanel;
	}

	public boolean isAddXOptionsPanel() {
		return addXOptionsPanel;
	}

	public boolean isAddYOptionsPanel() {
		return addYOptionsPanel;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	/** creates a menubar config object from a given json object **/
	public static MenuBarConfig createMenuBarConfigFromJSONObject(JSONObject o) {
		Dimension size = GuiOptions.visualizerDefaultMenuBarSize;
		boolean addCoordsPanel = true;
		boolean addIntervalPanel = true;
		boolean addXOptionsPanel = true;
		boolean addYOptionsPanel = true;

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

		return new MenuBarConfig(size, addCoordsPanel, addIntervalPanel,
				addXOptionsPanel, addYOptionsPanel);
	}
}
