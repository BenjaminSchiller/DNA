package dna.io;

import dna.series.DiffData;
import dna.series.MetricData;
import dna.series.RunData;
import dna.settings.Prefix;

/**
 * 
 * Determines the default path for RunData, DiffData, and MetricData objects.
 * 
 * @author benni
 * 
 */
public class Dir {
	public static String getRunDataDir(String seriesDir, RunData runData) {
		return seriesDir + runData.getRun() + "/";
	}

	public static String getDiffDataDir(String seriesDir, RunData runData,
			DiffData diffData) {
		return Dir.getRunDataDir(seriesDir, runData) + diffData.getTimestamp()
				+ "/";
	}

	public static String getMetricDataDir(String seriesDir, RunData runData,
			DiffData diffData, MetricData metricData) {
		return Dir.getDiffDataDir(seriesDir, runData, diffData)
				+ Prefix.metricDataDir + metricData.getName() + "/";
	}
}
