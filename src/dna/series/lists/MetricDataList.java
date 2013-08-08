package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.data.MetricData;

public class MetricDataList extends List<MetricData> {

	public MetricDataList() {
		super();
	}

	public MetricDataList(int size) {
		super(size);
	}

	public void write(String dir) throws IOException {
		for (MetricData metricData : this.getList()) {
			metricData.write(Dir.getMetricDataDir(dir, metricData.getName()));
		}
	}

	public static MetricDataList read(String dir, boolean readValues)
			throws IOException {
		String[] metrics = Dir.getMetrics(dir);
		MetricDataList list = new MetricDataList(metrics.length);
		for (String metric : metrics) {
			list.add(MetricData.read(dir + metric + Dir.delimiter,
					Dir.getMetricName(metric), readValues));
		}
		return list;
	}
}
