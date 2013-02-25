package dna.plot;

import dna.io.Path;
import dna.series.DiffData;
import dna.series.Distribution;
import dna.series.MetricData;
import dna.series.RunData;

public class Plotting {
	public static void plotRun(RunData runData, String seriesDir, String dstDir) {
		for (MetricData metric : runData.getDiffs().get(0).getMetrics()) {
			System.out.println("plotting " + metric.getName());
			for (Distribution distribution : metric.getDistributions()) {
				System.out.println("plotting " + distribution.getName());
				for (DiffData diffData : runData.getDiffs()) {
					MetricData metricData = diffData
							.getMetric(metric.getName());
					Distribution d = metricData.getDistribution(distribution
							.getName());
					String path = Path.getPath(seriesDir, runData, diffData,
							metricData) + d.getFilename();
					System.out.println("    => " + path);
				}
			}
		}
	}
}
