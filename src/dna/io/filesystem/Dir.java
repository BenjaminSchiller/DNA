package dna.io.filesystem;

import java.io.File;

import dna.io.filter.PrefixFilenameFilter;

/**
 * 
 * Gives the default storage path for data objects.
 * 
 * @author benni
 * 
 */
public class Dir {
	public static final String delimiter = "/";

	/*
	 * AGGREGATION
	 */

	public static String getAggregationDataDir(String dir) {
		return dir + Names.runAggregation + Dir.delimiter;
	}

	/*
	 * RUN data
	 */

	public static String getRunDataDir(String dir, int run) {
		return dir + Prefix.runDataDir + run + Dir.delimiter;
	}

	public static String[] getRuns(String dir) {
		return (new File(dir))
				.list(new PrefixFilenameFilter(Prefix.runDataDir));
	}

	public static int getRun(String runFolderName) {
		return Integer.parseInt(runFolderName.replaceFirst(Prefix.runDataDir,
				""));
	}

	/*
	 * DIFF data
	 */

	public static String getDiffDataDir(String dir, long timestamp) {
		return dir + Prefix.diffDataDir + timestamp + Dir.delimiter;
	}

	public static String getDiffDataDir(String dir, int run, long timestamp) {
		return Dir.getRunDataDir(dir, run) + Prefix.diffDataDir + timestamp
				+ Dir.delimiter;
	}

	public static String[] getDiffs(String dir) {
		return (new File(dir))
				.list(new PrefixFilenameFilter(Prefix.diffDataDir));
	}

	public static long getTimestamp(String diffFolderName) {
		return Long.parseLong(diffFolderName.replaceFirst(Prefix.diffDataDir,
				""));
	}

	/*
	 * METRIC data
	 */

	public static String getMetricDataDir(String dir, String name) {
		return dir + Prefix.metricDataDir + name + Dir.delimiter;
	}

	public static String getMetricDataDir(String dir, int run, long timestamp,
			String name) {
		return Dir.getDiffDataDir(dir, run, timestamp) + Prefix.metricDataDir
				+ name + Dir.delimiter;
	}

	public static String[] getMetrics(String dir) {
		return (new File(dir)).list(new PrefixFilenameFilter(
				Prefix.metricDataDir));
	}

	public static String getMetricName(String metricFolderName) {
		return metricFolderName.replaceFirst(Prefix.metricDataDir, "");
	}
}
