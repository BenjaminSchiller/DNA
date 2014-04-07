package dna.visualization.config.components;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import dna.visualization.MainDisplay;
import dna.visualization.config.JSON.JSONArray;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object to configure stats display windows.
 * 
 * @author Rwilmes
 */
public class StatsDisplayConfig {

	/**
	 * Configuration object to configure runtime windows in a statsdisplay.
	 * 
	 * @author Rwilmes
	 */
	public static class RunTimeConfig {

		// constructor
		public RunTimeConfig(String name, String[] names,
				boolean showHidePolicyFlag) {
			this.name = name;
			this.names = names;
			this.showHidePolicyFlag = showHidePolicyFlag;
		}

		// if true, all defined runtimes will be shown, all undefined hidden
		// if false, all defined runtimes will be hidden, all undefined shown
		private boolean showHidePolicyFlag;
		private String[] names;
		private String name;

		// get methods
		public boolean isAllShown() {
			return this.showHidePolicyFlag;
		}

		public String[] getNames() {
			return this.names;
		}

		public String getName() {
			return this.name;
		}
	}

	// constructor
	public StatsDisplayConfig(String name, Dimension size,
			Dimension settingsPanelSize, SimpleDateFormat dateFormat,
			String[] shownStatistics, boolean addTimePanel,
			Dimension timeSliderButtonSize, boolean addSpeedSlider,
			boolean addSettingsPanel, boolean addMetRuntimes,
			RunTimeConfig metricRuntimeConfig, boolean addGenRuntimes,
			RunTimeConfig generalRuntimeConfig) {
		this.name = name;
		this.size = size;
		this.settingsPanelSize = settingsPanelSize;
		this.dateFormat = dateFormat;
		this.shownStatistics = shownStatistics;
		this.addTimePanel = addTimePanel;
		this.timeSliderButtonSize = timeSliderButtonSize;
		this.addSpeedSlider = addSpeedSlider;
		this.addSettingsPanel = addSettingsPanel;
		this.addMetRuntimes = addMetRuntimes;
		this.metricRuntimeConfig = metricRuntimeConfig;
		this.addGenRuntimes = addGenRuntimes;
		this.generalRuntimeConfig = generalRuntimeConfig;
	}

	// general options
	private String name;
	private Dimension size;
	private Dimension settingsPanelSize;
	private Dimension timeSliderButtonSize;
	private SimpleDateFormat dateFormat;

	// statistics
	private String[] shownStatistics;

	// flags
	private boolean addTimePanel;
	private boolean addSpeedSlider;
	private boolean addSettingsPanel;
	private boolean addMetRuntimes;
	private boolean addGenRuntimes;

	// runtime configs
	private RunTimeConfig metricRuntimeConfig;
	private RunTimeConfig generalRuntimeConfig;

	// get methods
	public String getName() {
		return this.name;
	}

	public Dimension getSize() {
		return this.size;
	}

	public Dimension getSettingsPanelSize() {
		return this.settingsPanelSize;
	}

	public Dimension getTimeSliderButtonsSize() {
		return this.timeSliderButtonSize;
	}

	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
	}

	public String[] getShownStatistics() {
		return this.shownStatistics;
	}

	public boolean isAddTimePanel() {
		return this.addTimePanel;
	}

	public boolean isAddSpeedSlider() {
		return this.addSpeedSlider;
	}

	public boolean isAddSettingsPanel() {
		return this.addSettingsPanel;
	}

	public boolean isAddMetRuntimes() {
		return this.addMetRuntimes;
	}

	public boolean isAddGenRuntimes() {
		return this.addGenRuntimes;
	}

	public RunTimeConfig getMetricRuntimeConfig() {
		return this.metricRuntimeConfig;
	}

	public RunTimeConfig getGeneralRuntimeConfig() {
		return this.generalRuntimeConfig;
	}

	/** Creates a stats display config object from a given json object. **/
	public static StatsDisplayConfig creatStatsDisplayConfigFromJSONObject(
			JSONObject o) {
		// init
		String name;
		Dimension size;
		Dimension settingsPanelSize;
		Dimension timeSliderButtonsSize;
		SimpleDateFormat dateFormat;
		ArrayList<String> shownStatistics = new ArrayList<String>();
		boolean addTimePanel;
		boolean addSpeedSlider;
		boolean addSettingsPanel;
		boolean addMetRuntimes;
		boolean addGenRuntimes;
		RunTimeConfig metricRuntimeConfig = null;
		boolean showOnlyDefinedMetricRuntimes;
		RunTimeConfig generalRuntimeConfig = null;
		boolean showOnlyDefinedGeneralRuntimes;

		// set default values
		if (MainDisplay.DefaultConfig == null) {
			// if the defaultconfig is not set, use this default values
			name = o.getString("Name");
			size = new Dimension(o.getInt("Width"), o.getInt("Height"));
			JSONObject timeSliderButtonsObject = o
					.getJSONObject("TimeSliderButtons");
			timeSliderButtonsSize = new Dimension(
					timeSliderButtonsObject.getInt("Width"),
					timeSliderButtonsObject.getInt("Height"));
			JSONObject settingsPanelObject = o.getJSONObject("SettingsPanel");
			settingsPanelSize = new Dimension(
					settingsPanelObject.getInt("Width"),
					settingsPanelObject.getInt("Height"));
			dateFormat = new SimpleDateFormat(o.getString("DateFormat"));
			JSONArray statisticNames = o.getJSONArray("ShownStatistics");
			for (int i = 0; i < statisticNames.length(); i++) {
				shownStatistics.add(statisticNames.getString(i));
			}
			addTimePanel = o.getBoolean("ShowTimePanel");
			addSpeedSlider = o.getBoolean("ShowSpeedSlider");
			addSettingsPanel = o.getBoolean("ShowSettingsPanel");
			addMetRuntimes = o.getBoolean("ShowMetricRuntimes");
			addGenRuntimes = o.getBoolean("ShowGeneralRuntimes");
			// metric runtimes
			JSONObject metRuntimeObject = o
					.getJSONObject("MetricRuntimeConfig");
			String metRuntimeName = metRuntimeObject.getString("Name");
			showOnlyDefinedMetricRuntimes = metRuntimeObject
					.getBoolean("ShowDefinedValues");
			JSONArray values = metRuntimeObject.getJSONArray("Values");
			String[] valuesArray = new String[values.length()];
			for (int i = 0; i < values.length(); i++) {
				valuesArray[i] = values.getString(i);
			}
			metricRuntimeConfig = new RunTimeConfig(metRuntimeName,
					valuesArray, showOnlyDefinedMetricRuntimes);
			// general runtimes
			JSONObject genRuntimeObject = o
					.getJSONObject("GeneralRuntimeConfig");
			String genRuntimeName = genRuntimeObject.getString("Name");
			showOnlyDefinedGeneralRuntimes = genRuntimeObject
					.getBoolean("ShowDefinedValues");
			JSONArray values2 = genRuntimeObject.getJSONArray("Values");
			String[] values2Array = new String[values2.length()];
			for (int i = 0; i < values2.length(); i++) {
				values2Array[i] = values2.getString(i);
			}
			generalRuntimeConfig = new RunTimeConfig(genRuntimeName,
					values2Array, showOnlyDefinedGeneralRuntimes);
			String[] shownStatisticsArray = new String[shownStatistics.size()];
			shownStatisticsArray = shownStatistics
					.toArray(shownStatisticsArray);

			return new StatsDisplayConfig(name, size, settingsPanelSize,
					dateFormat, shownStatisticsArray, addTimePanel,
					timeSliderButtonsSize, addSpeedSlider, addSettingsPanel,
					addMetRuntimes, metricRuntimeConfig, addGenRuntimes,
					generalRuntimeConfig);
		} else {
			// use default config values as defaults
			name = MainDisplay.DefaultConfig.getStatsDisplayConfig().getName();
			size = MainDisplay.DefaultConfig.getStatsDisplayConfig().getSize();
			settingsPanelSize = MainDisplay.DefaultConfig
					.getStatsDisplayConfig().getSettingsPanelSize();
			dateFormat = MainDisplay.DefaultConfig.getStatsDisplayConfig()
					.getDateFormat();
			addTimePanel = MainDisplay.DefaultConfig.getStatsDisplayConfig()
					.isAddTimePanel();
			timeSliderButtonsSize = MainDisplay.DefaultConfig
					.getStatsDisplayConfig().getTimeSliderButtonsSize();
			addSpeedSlider = MainDisplay.DefaultConfig.getStatsDisplayConfig()
					.isAddSpeedSlider();
			addSettingsPanel = MainDisplay.DefaultConfig
					.getStatsDisplayConfig().isAddSettingsPanel();
			addMetRuntimes = MainDisplay.DefaultConfig.getStatsDisplayConfig()
					.isAddMetRuntimes();
			addGenRuntimes = MainDisplay.DefaultConfig.getStatsDisplayConfig()
					.isAddGenRuntimes();
			metricRuntimeConfig = MainDisplay.DefaultConfig
					.getStatsDisplayConfig().getMetricRuntimeConfig();
			generalRuntimeConfig = MainDisplay.DefaultConfig
					.getStatsDisplayConfig().getGeneralRuntimeConfig();
		}

		// overwrite default values with parsed values
		try {
			name = o.getString("Name");
		} catch (Exception e) {
		}

		try {
			size = new Dimension(o.getInt("Width"), o.getInt("Height"));
		} catch (Exception e) {
		}

		try {
			JSONObject timeSliderButtonsObject = o
					.getJSONObject("TimeSliderButtons");
			timeSliderButtonsSize = new Dimension(
					timeSliderButtonsObject.getInt("Width"),
					timeSliderButtonsObject.getInt("Height"));
		} catch (Exception e) {
		}

		try {
			JSONObject settingsPanelObject = o.getJSONObject("SettingsPanel");
			settingsPanelSize = new Dimension(
					settingsPanelObject.getInt("Width"),
					settingsPanelObject.getInt("Height"));
		} catch (Exception e) {
		}

		try {
			dateFormat = new SimpleDateFormat(o.getString("DateFormat"));
		} catch (Exception e) {
		}

		try {
			JSONArray statisticNames = o.getJSONArray("ShownStatistics");
			for (int i = 0; i < statisticNames.length(); i++) {
				shownStatistics.add(statisticNames.getString(i));
			}
		} catch (Exception e) {
		}

		try {
			addTimePanel = o.getBoolean("ShowTimePanel");
		} catch (Exception e) {
		}

		try {
			addSpeedSlider = o.getBoolean("ShowSpeedSlider");
		} catch (Exception e) {
		}

		try {
			addSettingsPanel = o.getBoolean("ShowSettingsPanel");
		} catch (Exception e) {
		}

		try {
			addMetRuntimes = o.getBoolean("ShowMetricRuntimes");
		} catch (Exception e) {
		}

		try {
			addGenRuntimes = o.getBoolean("ShowGeneralRuntimes");
		} catch (Exception e) {
		}

		try {
			JSONObject metRuntimeObject = o
					.getJSONObject("MetricRuntimeConfig");

			String metRuntimeName = "MetricRuntimes";
			try {
				metRuntimeName = metRuntimeObject.getString("Name");
			} catch (Exception e) {
			}

			showOnlyDefinedMetricRuntimes = metRuntimeObject
					.getBoolean("ShowDefinedValues");
			JSONArray values = metRuntimeObject.getJSONArray("Values");
			String[] valuesArray = new String[values.length()];
			for (int i = 0; i < values.length(); i++) {
				valuesArray[i] = values.getString(i);
			}
			metricRuntimeConfig = new RunTimeConfig(metRuntimeName,
					valuesArray, showOnlyDefinedMetricRuntimes);
		} catch (Exception e) {
		}

		try {
			JSONObject genRuntimeObject = o
					.getJSONObject("GeneralRuntimeConfig");

			String genRuntimeName = "GeneralRuntimes";
			try {
				genRuntimeName = genRuntimeObject.getString("Name");
			} catch (Exception e) {
			}

			showOnlyDefinedGeneralRuntimes = genRuntimeObject
					.getBoolean("ShowDefinedValues");
			JSONArray values = genRuntimeObject.getJSONArray("Values");
			String[] valuesArray = new String[values.length()];
			for (int i = 0; i < values.length(); i++) {
				valuesArray[i] = values.getString(i);
			}
			generalRuntimeConfig = new RunTimeConfig(genRuntimeName,
					valuesArray, showOnlyDefinedGeneralRuntimes);
		} catch (Exception e) {
		}

		String[] shownStatisticsArray = new String[shownStatistics.size()];
		shownStatisticsArray = shownStatistics.toArray(shownStatisticsArray);

		return new StatsDisplayConfig(name, size, settingsPanelSize,
				dateFormat, shownStatisticsArray, addTimePanel,
				timeSliderButtonsSize, addSpeedSlider, addSettingsPanel,
				addMetRuntimes, metricRuntimeConfig, addGenRuntimes,
				generalRuntimeConfig);
	}
}
