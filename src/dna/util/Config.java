package dna.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import dna.plot.Gnuplot.PlotStyle;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotType;

public class Config {
	private static Properties properties;

	private static HashMap<String, String> overwrite;

	private static String defaultConfigFolder = "config/";

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
		switch (Config.get(key)) {
		case ("lines"):
			return PlotStyle.lines;
		case ("dots"):
			return PlotStyle.dots;
		case ("points"):
			return PlotStyle.points;
		case ("linespoint"):
			return PlotStyle.linespoint;
		case ("impulses"):
			return PlotStyle.impulses;
		case ("steps"):
			return PlotStyle.steps;
		case ("boxes"):
			return PlotStyle.boxes;
		case ("candlesticks"):
			return PlotStyle.candlesticks;
		case ("yerrorbars"):
			return PlotStyle.yerrorbars;
		default:
			return PlotStyle.linespoint;
		}
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

	public static void addFile(String file) throws IOException {
		if (properties == null) {
			// System.out.println("initializing with " + file);
			properties = new java.util.Properties();
			FileInputStream in = new FileInputStream(file);
			properties.load(in);
		} else {
			// System.out.println("adding " + file);
			Properties temp = new java.util.Properties();
			FileInputStream in = new FileInputStream(file);
			temp.load(in);
			properties.putAll(temp);
		}
	}

	public static void initWithFiles(String[] file) throws IOException {
		properties = null;
		overwrite = null;
		for (int i = 0; i < file.length; i++) {
			addFile(file[i]);
		}
	}

	public static void initWithFolders(String[] folders) throws IOException {
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < folders.length; i++) {
			File folder = new File(folders[i]);
			File[] list = folder.listFiles();
			for (int j = 0; j < list.length; j++) {
				if (list[j].isFile()
						&& list[j].getAbsolutePath().endsWith(".properties")) {
					v.add(list[j].getAbsolutePath());
				}
			}
		}
		initWithFiles(ArrayUtils.toStringArray(v));
	}

	public static void initWithFile(String file) throws IOException {
		initWithFiles(new String[] { file });
	}

	public static void initWithFolder(String folder) throws IOException {
		initWithFolders(new String[] { folder });
	}

	public static void init() throws IOException {
		Vector<String> v = new Vector<String>();
		File folder = new File(defaultConfigFolder);
		File[] list = folder.listFiles();
		v.add(folder.getAbsolutePath());
		for (int j = 0; j < list.length; j++) {
			if (list[j].isDirectory() && !list[j].getName().startsWith(".")) {
				v.add(list[j].getAbsolutePath());
			}
		}
		initWithFolders(ArrayUtils.toStringArray(v));
	}

	public static boolean containsKey(String key) {
		return properties.containsKey(key);
	}

	public static String[] keys(String from) {
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
}
