package dna.visualization.config.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.jar.JarFile;

import dna.util.Config;
import dna.visualization.BatchHandler.ZipMode;
import dna.visualization.MainDisplay;
import dna.visualization.config.JSON.JSONObject;
import dna.visualization.config.JSON.JSONTokener;

public class MainDisplayConfig {

	// constructor
	public MainDisplayConfig(String name, Dimension size,
			boolean liveDisplayMode, boolean fullscreen, ZipMode zipMode,
			String defaultDir, Font defaultFont, Color defaultFontColor,
			Dimension buttonSize, String logoDir, Dimension logoSize,
			String scalingExpression, Dimension visualizerPanelSize,
			Dimension innerVisualizerPanelSize,
			StatsDisplayConfig statsDisplayConfig,
			LogDisplayConfig[] logDisplayConfigs,
			MetricVisualizerConfig[] metricVisualizerConfigs,
			MultiScalarVisualizerConfig[] multiScalarVisualizerConfigs) {
		this.name = name;
		this.size = size;
		this.liveDisplayMode = liveDisplayMode;
		this.fullscreen = fullscreen;
		this.zipMode = zipMode;
		this.defaultDir = defaultDir;
		this.defaultFont = defaultFont;
		this.defaultFontColor = defaultFontColor;
		this.buttonSize = buttonSize;
		this.logoDir = logoDir;
		this.logoSize = logoSize;
		this.scalingExpression = scalingExpression;
		this.visualizerPanelSize = visualizerPanelSize;
		this.innerVisualizerPanelSize = innerVisualizerPanelSize;
		this.statsDisplayConfig = statsDisplayConfig;
		this.logDisplayConfigs = logDisplayConfigs;
		this.metricVisualizerConfigs = metricVisualizerConfigs;
		this.multiScalarVisualizerConfigs = multiScalarVisualizerConfigs;
	}

	private String name;
	private Dimension size;
	private boolean liveDisplayMode;
	private boolean fullscreen;
	private ZipMode zipMode;
	private String defaultDir;
	private Font defaultFont;
	private Color defaultFontColor;

	private Dimension buttonSize;
	private String logoDir;
	private Dimension logoSize;
	private String scalingExpression;
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

	public void setDefaultDir(String defaultDir) {
		this.defaultDir = defaultDir;
	}

	public boolean isLiveDisplayMode() {
		return this.liveDisplayMode;
	}

	public void setLiveDisplayMode(boolean liveDisplayMode) {
		this.liveDisplayMode = liveDisplayMode;
	}

	public boolean isFullscreen() {
		return this.fullscreen;
	}

	public boolean isBatchesZipped() {
		if (this.zipMode.equals(ZipMode.batches))
			return true;
		else
			return false;
	}

	public boolean isRunsZipped() {
		if (this.zipMode.equals(ZipMode.runs))
			return true;
		else
			return false;
	}

	public ZipMode getZipMode() {
		return this.zipMode;
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

	public String getScalingExpression() {
		return this.scalingExpression;
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
		// init
		String name;
		Dimension size;
		boolean liveDisplayMode;
		boolean fullscreen;
		boolean batchesZipped;
		ZipMode zipMode;
		String defaultDir;
		Font defaultFont;
		Color defaultFontColor;
		Dimension buttonSize;
		String logoDir;
		Dimension logoSize;
		String scalingExpression;
		Dimension visualizerPanelSize;
		Dimension innerVisualizerPanelSize;

		StatsDisplayConfig statsDisplayConfig = null;
		LogDisplayConfig[] logDisplayConfigs = new LogDisplayConfig[0];
		MetricVisualizerConfig[] metricVisualizerConfigs = new MetricVisualizerConfig[0];
		MultiScalarVisualizerConfig[] multiScalarVisualizerConfigs = new MultiScalarVisualizerConfig[0];

		// set default values
		if (MainDisplay.DefaultConfig == null) {
			// if the defaultconfig is not set, read default values
			name = o.getString("Name");
			size = new Dimension(o.getInt("Width"), o.getInt("Height"));
			liveDisplayMode = o.getBoolean("LiveDisplayMode");
			fullscreen = o.getBoolean("Fullscreen");
			String zipM = o.getString("ZipMode");
			switch (zipM) {
			case "batches":
				zipMode = ZipMode.batches;
				break;
			case "runs":
				zipMode = ZipMode.runs;
				break;
			default:
				zipMode = ZipMode.none;
				break;
			}

			defaultDir = o.getString("DefaultDir");

			JSONObject fontObject = o.getJSONObject("DefaultFont");
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
			defaultFontColor = Color.BLACK;
			try {
				Field field = Color.class.getField(fontObject
						.getString("Color"));
				defaultFontColor = (Color) field.get(null);
			} catch (Exception e) {
			}
			JSONObject buttonObject = o.getJSONObject("Buttons");
			buttonSize = new Dimension(buttonObject.getInt("Width"),
					buttonObject.getInt("Height"));
			JSONObject logoObject = o.getJSONObject("Logo");
			logoDir = logoObject.getString("Dir");
			logoSize = new Dimension(logoObject.getInt("Width"),
					logoObject.getInt("Height"));
			scalingExpression = o.getString("Scaling");
			JSONObject visPanelObject = o.getJSONObject("VisualizerPanel");
			visualizerPanelSize = new Dimension(visPanelObject.getInt("Width"),
					visPanelObject.getInt("Height"));
			innerVisualizerPanelSize = new Dimension(
					visPanelObject.getInt("InnerWidth"),
					visPanelObject.getInt("InnerHeight"));
		} else {
			// use default config values as defaults
			name = MainDisplay.DefaultConfig.getName();
			size = MainDisplay.DefaultConfig.getSize();
			liveDisplayMode = MainDisplay.DefaultConfig.isLiveDisplayMode();
			fullscreen = MainDisplay.DefaultConfig.isFullscreen();
			zipMode = MainDisplay.DefaultConfig.getZipMode();
			defaultDir = MainDisplay.DefaultConfig.getDefaultDir();
			defaultFont = MainDisplay.DefaultConfig.getDefaultFont();
			defaultFontColor = MainDisplay.DefaultConfig.getDefaultFontColor();
			buttonSize = MainDisplay.DefaultConfig.getButtonSize();
			logoDir = MainDisplay.DefaultConfig.getLogoDir();
			logoSize = MainDisplay.DefaultConfig.getLogoSize();
			scalingExpression = MainDisplay.DefaultConfig
					.getScalingExpression();
			visualizerPanelSize = MainDisplay.DefaultConfig
					.getVisualizerPanelSize();
			innerVisualizerPanelSize = MainDisplay.DefaultConfig
					.getInnerVisualizerPanelSize();
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
			liveDisplayMode = o.getBoolean("LiveDisplayMode");
		} catch (Exception e) {
		}

		try {
			fullscreen = o.getBoolean("Fullscreen");
		} catch (Exception e) {
		}

		try {
			batchesZipped = o.getBoolean("BatchesZipped");
		} catch (Exception e) {
		}

		try {
			String mode = o.getString("ZipMode");
			switch (mode) {
			case "batches":
				zipMode = ZipMode.batches;
				break;
			case "runs":
				zipMode = ZipMode.runs;
				break;
			default:
				zipMode = ZipMode.none;
				break;
			}
		} catch (Exception e) {
		}

		try {
			defaultDir = o.getString("DefaultDir");
		} catch (Exception e) {
		}

		try {
			scalingExpression = o.getString("Scaling");
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

		return new MainDisplayConfig(name, size, liveDisplayMode, fullscreen,
				zipMode, defaultDir, defaultFont, defaultFontColor, buttonSize,
				logoDir, logoSize, scalingExpression, visualizerPanelSize,
				innerVisualizerPanelSize, statsDisplayConfig,
				logDisplayConfigs, metricVisualizerConfigs,
				multiScalarVisualizerConfigs);
	}

	/** Reads a MainDisplayConfig from the given path. **/
	public static MainDisplayConfig getConfig(String path) {
		return MainDisplayConfig.getConfig(path, false);
	}

	/** Reads a MainDisplayConfig from the given path. **/
	public static MainDisplayConfig getConfig(String path, boolean fromJar) {
		InputStream is = null;
		JarFile x = null;

		if (fromJar) {
			try {
				Path pPath = Paths.get(Config.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI());
				String[] splits = path.split("/");
				x = new JarFile(pPath.toFile(), false);
				is = x.getInputStream(x.getEntry(splits[splits.length - 1]));
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				is = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		JSONTokener tk = new JSONTokener(is);
		JSONObject jsonConfig = new JSONObject(tk);
		MainDisplayConfig config = MainDisplayConfig
				.createMainDisplayConfigFromJSONObject(jsonConfig
						.getJSONObject("MainDisplayConfig"));

		// close jar-file if necessary
		if (x != null) {
			try {
				x.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return config;
	}

	/** Reads a MainDisplayConfig from the given path inside the jar. **/
	public static MainDisplayConfig getConfigFromJar(String path) {
		return MainDisplayConfig.getConfig(path, true);
	}

	/**
	 * Reads a MainDisplayConfig from the given path but replaces the default
	 * data dir.
	 **/
	public static MainDisplayConfig getConfig(String path, String dataDir) {
		MainDisplayConfig cfg = MainDisplayConfig.getConfig(path);
		cfg.setDefaultDir(dataDir);
		return cfg;
	}
}
