package dna.util;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

public class Config extends PropertiesHolder {
	private static Properties properties;

	private static HashMap<String, String> overwrite;

	private static String defaultConfigFolder = "config/";

	public static void setConfigFolder(String cf) {
		defaultConfigFolder = cf;
	}

	public static String get(String key) {
		String temp = null;
		if (overwrite != null && (temp = overwrite.get(key)) != null) {
			return temp;
		}
		if (properties == null) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return properties.getProperty(key);
	}

	public static Properties getProperties() {
		if (Config.properties == null) {
			try {
				Config.init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Config.properties;
	}

	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}

	public static int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	public static double getDouble(String key) {
		return Double.parseDouble(get(key));
	}

	public static float getFloat(String key) {
		return Float.parseFloat(get(key));
	}

	public static NodeValueListOrderBy getNodeValueListOrderBy(String key) {
		switch (Config.get(key)) {
		case ("index"):
			return NodeValueListOrderBy.index;
		case ("average"):
			return NodeValueListOrderBy.average;
		case ("median"):
			return NodeValueListOrderBy.median;
		case ("minimum"):
			return NodeValueListOrderBy.minimum;
		case ("maximum"):
			return NodeValueListOrderBy.maximum;
		case ("variance"):
			return NodeValueListOrderBy.variance;
		case ("varianceLow"):
			return NodeValueListOrderBy.varianceLow;
		case ("varianceUp"):
			return NodeValueListOrderBy.varianceUp;
		case ("confidenceLow"):
			return NodeValueListOrderBy.confidenceLow;
		case ("confidenceUp"):
			return NodeValueListOrderBy.confidenceUp;
		default:
			return NodeValueListOrderBy.index;
		}
	}

	public static NodeValueListOrder getNodeValueListOrder(String key) {
		switch (Config.get(key)) {
		case "ascending":
			return NodeValueListOrder.ascending;
		case "descending":
			return NodeValueListOrder.descending;
		default:
			return NodeValueListOrder.ascending;
		}
	}

	public static DistributionPlotType getDistributionPlotType(String key) {
		switch (Config.get(key)) {
		case "distOnly":
			return DistributionPlotType.distOnly;
		case "cdfOnly":
			return DistributionPlotType.cdfOnly;
		case "distANDcdf":
			return DistributionPlotType.distANDcdf;
		default:
			return DistributionPlotType.distOnly;
		}
	}

	public static SortModeNVL getSortModeNVL(String key) {
		switch (Config.get(key)) {
		case "index":
			return SortModeNVL.index;
		case "ascending":
			return SortModeNVL.ascending;
		case "descending":
			return SortModeNVL.descending;
		default:
			return SortModeNVL.ascending;
		}
	}

	public static SortModeDist getSortModeDist(String key) {
		switch (Config.get(key)) {
		case "distribution":
			return SortModeDist.distribution;
		case "cdf":
			return SortModeDist.cdf;
		default:
			return SortModeDist.distribution;
		}
	}

	public static xAxisSelection getXAxisSelection(String key) {
		switch (Config.get(key)) {
		case "x1":
			return xAxisSelection.x1;
		case "x2":
			return xAxisSelection.x2;
		default:
			return xAxisSelection.x1;
		}
	}

	public static yAxisSelection getYAxisSelection(String key) {
		switch (Config.get(key)) {
		case "y1":
			return yAxisSelection.y1;
		case "y2":
			return yAxisSelection.y2;
		default:
			return yAxisSelection.y1;
		}
	}

	public static Color getColor(String key) {
		Color color;
		try {
			Field field = Color.class.getField(Config.get(key));
			color = (Color) field.get(null);
		} catch (Exception e) {
			color = null;
			e.printStackTrace();
		}
		return color;
	}

	/**
	 * Reads a font from config file. Note: Needs a name, style and size!
	 * Example: getFont("GUI_DEFAULT_FONT") reads "GUI_DEFAULT_FONT_NAME",
	 * "GUI_DEFAULT_SONT_STYLE" and "GUI_DEFAULT_FONT_SIZE"
	 * 
	 * @param key
	 * @return
	 */
	public static Font getFont(String key) {
		String name = Config.get(key + "_NAME");
		String tempStyle = Config.get(key + "_STYLE");
		int size = Config.getInt(key + "_SIZE");
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

		return new Font(name, style, size);
	}

	public static PlotType getPlotType(String key) {
		switch (Config.get(key)) {
		case ("average"):
			return PlotType.average;
		case ("median"):
			return PlotType.median;
		case ("minimum"):
			return PlotType.minimum;
		case ("maximum"):
			return PlotType.maximum;
		case ("variance"):
			return PlotType.variance;
		case ("confidence1"):
			return PlotType.confidence1;
		case ("confidence2"):
			return PlotType.confidence2;
		case ("function"):
			return PlotType.function;
		default:
			return PlotType.average;
		}
	}

	public static PlotStyle getPlotStyle(String key) {
		if (Config.get(key) == null) {
			return null;
		}
		return PlotStyle.valueOf(Config.get(key));
	}

	public static void appendToList(String key, String value) {
		String oldValue = Config.get(key);
		if (oldValue == null || oldValue.length() == 0) {
			Config.overwrite(key, value);
		} else if (!oldValue.contains(value)) {
			Config.overwrite(key,
					oldValue + Config.get("CONFIG_LIST_SEPARATOR") + value);
		}
	}

	public static void overwrite(String key, String value) {
		try {
			if (properties == null) {
				try {
					init();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			overwrite.put(key, value);
		} catch (NullPointerException e) {
			overwrite = new HashMap<String, String>();
			overwrite.put(key, value);
		}
	}

	public static void reset(String key) {
		if (overwrite != null) {
			if (overwrite.containsKey(key)) {
				overwrite.remove(key);
			}
		}
	}

	public static void resetAll() {
		overwrite = new HashMap<String, String>();
	}

	public static void loadFromProperties(Properties in) {
		if (properties == null) {
			properties = new java.util.Properties();
		}
		properties.putAll(in);
	}

	public static void init() throws IOException {
		properties = null;
		overwrite = null;

		Vector<File> folders = new Vector<File>();
		folders.add(new File(defaultConfigFolder));

		try {
			Path pPath = Paths.get(Config.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
			if (pPath.getFileName().toString().endsWith(".jar")) {
				folders.add(pPath.toFile());
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		loadFromProperties(initFromFolders(folders));
	}

	public static boolean containsKey(String key) {
		if (properties == null) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return properties.containsKey(key);
	}

	public static String[] keys(String from) {
		String key = Config.get(from);
		if (key == null) {
			Log.debug("null string received when reading keys from '" + from
					+ "'. Returning empty string array.");
			return new String[0];
		}

		String[] keys = Config.get(from).split(
				Config.get("CONFIG_LIST_SEPARATOR"));
		for (int i = 0; i < keys.length; i++) {
			keys[i] = keys[i].trim();
		}
		if (keys.length == 1 && keys[0].length() == 0) {
			return new String[] {};
		}
		return keys;
	}

	public static String getMetricDescription(String metric) {
		if (Config.containsKey(metric + "_descr")) {
			return Config.get(metric + "_descr");
		} else if (Config.containsKey(metric + "_extends")) {
			return Config.getMetricDescription(Config.get(metric + "_extends"));
		} else {
			return null;
		}
	}

	public static String getPropertyDescription(String metric, String property) {
		if (Config.containsKey(metric + "_" + property + "_descr")) {
			return Config.get(metric + "_" + property + "_descr");
		} else if (Config.containsKey(metric + "_extends")) {
			return Config.getPropertyDescription(
					Config.get(metric + "_extends"), property);
		} else {
			return null;
		}
	}
}
