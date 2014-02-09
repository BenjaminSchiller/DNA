package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.ArrayList;

import dna.visualization.GuiOptions;
import dna.visualization.config.JSON.JSONObject;

public class MainDisplayConfig {

	// constructor
	public MainDisplayConfig(String name, Dimension size, boolean fullscreen,
			String defaultDir, Font defaultFont, Color defaultFontColor,
			Dimension buttonSize, String logoDir, Dimension logoSize,
			Dimension visualizerPanelSize, Dimension innerVisualizerPanelSize,
			StatsDisplayConfig statsDisplayConfig,
			LogDisplayConfig[] logDisplayConfigs,
			MetricVisualizerConfig[] metricVisualizerConfigs,
			MultiScalarVisualizerConfig[] multiScalarVisualizerConfigs) {
		this.name = name;
		this.size = size;
		this.fullscreen = fullscreen;
		this.defaultDir = defaultDir;
		this.defaultFont = defaultFont;
		this.defaultFontColor = defaultFontColor;
		this.buttonSize = buttonSize;
		this.logoDir = logoDir;
		this.logoSize = logoSize;
		this.visualizerPanelSize = visualizerPanelSize;
		this.innerVisualizerPanelSize = innerVisualizerPanelSize;
		this.statsDisplayConfig = statsDisplayConfig;
		this.logDisplayConfigs = logDisplayConfigs;
		this.metricVisualizerConfigs = metricVisualizerConfigs;
		this.multiScalarVisualizerConfigs = multiScalarVisualizerConfigs;
	}

	private String name;
	private Dimension size;
	private boolean fullscreen;
	private String defaultDir;
	private Font defaultFont;
	private Color defaultFontColor;

	private Dimension buttonSize;
	private String logoDir;
	private Dimension logoSize;
	private Dimension visualizerPanelSize;
	private Dimension innerVisualizerPanelSize;

	private StatsDisplayConfig statsDisplayConfig;
	private LogDisplayConfig[] logDisplayConfigs;
	private MetricVisualizerConfig[] metricVisualizerConfigs;
	private MultiScalarVisualizerConfig[] multiScalarVisualizerConfigs;

	// get methods
	public String getName() {
		return this.name;
	}

	public Dimension getSize() {
		return this.size;
	}

	public String getDefaultDir() {
		return this.defaultDir;
	}

	public boolean isFullscreen() {
		return this.fullscreen;
	}

	public Font getDefaultFont() {
		return this.defaultFont;
	}

	public Color getDefaultFontColor() {
		return this.defaultFontColor;
	}

	public Dimension getButtonSize() {
		return this.buttonSize;
	}

	public String getLogoDir() {
		return this.logoDir;
	}

	public Dimension getLogoSize() {
		return this.logoSize;
	}

	public Dimension getVisualizerPanelSize() {
		return this.visualizerPanelSize;
	}

	public Dimension getInnerVisualizerPanelSize() {
		return this.innerVisualizerPanelSize;
	}

	public StatsDisplayConfig getStatsDisplayConfig() {
		return this.statsDisplayConfig;
	}

	public LogDisplayConfig[] getLogDisplayConfigs() {
		return this.logDisplayConfigs;
	}

	public MetricVisualizerConfig[] getMetricVisualizerConfigs() {
		return this.metricVisualizerConfigs;
	}

	public MultiScalarVisualizerConfig[] getMultiScalarVisualizerConfigs() {
		return this.multiScalarVisualizerConfigs;
	}

	/** Creates a main display config object from a given json object. **/
	public static MainDisplayConfig createMainDisplayConfigFromJSONObject(
			JSONObject o) {
		String name = GuiOptions.mainDisplayDefaultTitle;
		Dimension size = GuiOptions.mainDisplayDefaultSize;
		boolean fullscreen = GuiOptions.mainDisplayDefaultIsFullscreen;
		String defaultDir = GuiOptions.defaultDir;
		Font defaultFont = GuiOptions.defaultFont;
		Color defaultFontColor = GuiOptions.defaultFontColor;
		Dimension buttonSize = GuiOptions.mainDisplayDefaultButtonSize;

		String logoDir = GuiOptions.mainDisplayDefaultLogoDir;
		Dimension logoSize = GuiOptions.mainDisplayDefaultLogoSize;

		Dimension visualizerPanelSize = GuiOptions.mainDisplayDefaultVisualizerPanelSize;
		Dimension innerVisualizerPanelSize = GuiOptions.mainDisplayDefaultVisualizerPanelSize;

		StatsDisplayConfig statsDisplayConfig = null;
		LogDisplayConfig[] logDisplayConfigs = new LogDisplayConfig[0];
		MetricVisualizerConfig[] metricVisualizerConfigs = new MetricVisualizerConfig[0];
		MultiScalarVisualizerConfig[] multiScalarVisualizerConfigs = new MultiScalarVisualizerConfig[0];

		try {
			name = o.getString("Name");
		} catch (Exception e) {
		}

		try {
			size = new Dimension(o.getInt("Width"), o.getInt("Height"));
		} catch (Exception e) {
		}

		try {
			fullscreen = o.getBoolean("Fullscreen");
		} catch (Exception e) {
		}

		try {
			defaultDir = o.getString("DefaultDir");
		} catch (Exception e) {
		}

		try {
			JSONObject fontObject = o.getJSONObject("DefaultFont");
			try {
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
				defaultFont = new Font(tempName, style, tempSize);
			} catch (Exception e) {
			}
			try {
				Field field = Color.class.getField(fontObject
						.getString("Color"));
				defaultFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		try {
			defaultDir = o.getString("DefaultDir");
		} catch (Exception e) {
		}

		try {
			JSONObject buttonObject = o.getJSONObject("Buttons");
			buttonSize = new Dimension(buttonObject.getInt("Width"),
					buttonObject.getInt("Height"));
		} catch (Exception e) {
		}

		try {
			JSONObject logoObject = o.getJSONObject("Logo");
			try {
				logoDir = logoObject.getString("Dir");
			} catch (Exception e) {
			}
			try {
				logoSize = new Dimension(logoObject.getInt("Width"),
						logoObject.getInt("Height"));
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		try {
			JSONObject visPanelObject = o.getJSONObject("VisualizerPanel");
			try {
				visualizerPanelSize = new Dimension(
						visPanelObject.getInt("Width"),
						visPanelObject.getInt("Height"));
			} catch (Exception e) {
			}
			try {
				innerVisualizerPanelSize = new Dimension(
						visPanelObject.getInt("InnerWidth"),
						visPanelObject.getInt("InnerHeight"));
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}

		try {
			statsDisplayConfig = StatsDisplayConfig
					.creatStatsDisplayConfigFromJSONObject(o
							.getJSONObject("StatsDisplayConfig"));
		} catch (Exception e) {
		}

		// gather log display configs
		ArrayList<LogDisplayConfig> logDisplayConfigsArray = new ArrayList<LogDisplayConfig>();
		try {
			JSONObject mvo = o.getJSONObject("LogDisplayConfigs");

			for (String logDis : JSONObject.getNames(mvo)) {
				try {
					logDisplayConfigsArray.add(LogDisplayConfig
							.createLogDisplayConfigFromJSONObject(mvo
									.getJSONObject(logDis)));
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		logDisplayConfigs = new LogDisplayConfig[logDisplayConfigsArray.size()];
		for (int i = 0; i < logDisplayConfigsArray.size(); i++) {
			logDisplayConfigs[i] = logDisplayConfigsArray.get(i);
		}

		// gather metric visualizer configs
		ArrayList<MetricVisualizerConfig> metricVisualizerConfigsArray = new ArrayList<MetricVisualizerConfig>();
		try {
			JSONObject mvo = o.getJSONObject("MetricVisualizerConfigs");

			for (String metricVis : JSONObject.getNames(mvo)) {
				try {
					metricVisualizerConfigsArray.add(MetricVisualizerConfig
							.createMetricVisualizerConfigFromJSONObject(mvo
									.getJSONObject(metricVis)));
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		metricVisualizerConfigs = new MetricVisualizerConfig[metricVisualizerConfigsArray
				.size()];
		for (int i = 0; i < metricVisualizerConfigsArray.size(); i++) {
			metricVisualizerConfigs[i] = metricVisualizerConfigsArray.get(i);
		}

		// gather multi scalar visualizer configs
		ArrayList<MultiScalarVisualizerConfig> multiScalarVisualizerConfigsArray = new ArrayList<MultiScalarVisualizerConfig>();
		try {
			JSONObject mvo = o.getJSONObject("MultiScalarVisualizerConfigs");

			for (String multiScalarVis : JSONObject.getNames(mvo)) {
				try {
					multiScalarVisualizerConfigsArray
							.add(MultiScalarVisualizerConfig
									.createMultiScalarVisualizerConfigFromJSONObject(mvo
											.getJSONObject(multiScalarVis)));
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		multiScalarVisualizerConfigs = new MultiScalarVisualizerConfig[multiScalarVisualizerConfigsArray
				.size()];
		for (int i = 0; i < multiScalarVisualizerConfigsArray.size(); i++) {
			multiScalarVisualizerConfigs[i] = multiScalarVisualizerConfigsArray
					.get(i);
		}

		return new MainDisplayConfig(name, size, fullscreen, defaultDir,
				defaultFont, defaultFontColor, buttonSize, logoDir, logoSize,
				visualizerPanelSize, innerVisualizerPanelSize,
				statsDisplayConfig, logDisplayConfigs, metricVisualizerConfigs,
				multiScalarVisualizerConfigs);
	}
}
