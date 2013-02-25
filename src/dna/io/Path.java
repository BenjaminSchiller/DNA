package dna.io;

import dna.series.DiffData;
import dna.series.MetricData;
import dna.series.RunData;

public class Path {
	public static String getPath(String seriesDir, RunData runData) {
		return seriesDir + runData.getRun() + "/";
	}

	public static String getPath(String seriesDir, RunData runData,
			DiffData diffData) {
		return Path.getPath(seriesDir, runData) + diffData.getTimestamp() + "/";
	}

	public static String getPath(String seriesDir, RunData runData,
			DiffData diffData, MetricData metricData) {
		return Path.getPath(seriesDir, runData, diffData) + metricData.getName()
				+ "/";
	}
}
