package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.MetricData;
import dna.util.Config;

public class MetricDataList extends List<MetricData> {

	public MetricDataList() {
		super();
	}

	public MetricDataList(int size) {
		super(size);
	}

	public void write(String dir) throws IOException {
		for (MetricData metricData : this.getList()) {
			String suffix;
			switch (metricData.getType()) {
			case exact:
				suffix = Config.get("SUFFIX_METRIC_EXACT");
				break;
			case heuristic:
				suffix = Config.get("SUFFIX_METRIC_HEURISTIC");
				break;
			case quality:
				suffix = "";
				;
				break;
			default:
				suffix = "";
				break;
			}

			metricData.write(Dir.getMetricDataDir(dir, metricData.getName()
					+ suffix));
		}
	}

	public static MetricDataList read(String dir, BatchReadMode batchReadMode)
			throws IOException {
		String[] metrics = Dir.getMetrics(dir);
		MetricDataList list = new MetricDataList(metrics.length);
		for (String metric : metrics) {
			list.add(MetricData.read(dir + metric + Dir.delimiter,
					Dir.getMetricName(metric), batchReadMode));
		}
		return list;
	}

	/** Returns the metric from the list with the most similarities to m1. **/
	public MetricData getBestMatchingComparisonMetric(MetricData m1) {
		int similarities = 0;
		MetricData bestMatch = null;

		for (MetricData m2 : this.getList()) {
			int temp = MetricData.countSimilarities(m1, m2);
			if (temp > similarities) {
				similarities = temp;
				bestMatch = m2;
			}
		}

		return bestMatch;
	}
}
