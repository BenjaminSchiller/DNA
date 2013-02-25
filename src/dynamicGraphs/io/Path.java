package dynamicGraphs.io;

import dynamicGraphs.series.DiffData;
import dynamicGraphs.series.MetricData;
import dynamicGraphs.series.RunData;

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
