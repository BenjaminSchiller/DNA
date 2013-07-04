package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.lists.List;

/**
 * An AggregatedMetricList is a list containing AggregatedMetric objects.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedMetricList extends List<AggregatedMetric> {

	// constructors
	public AggregatedMetricList() {
		super();
	}

	public AggregatedMetricList(int size) {
		super(size);
	}
<<<<<<< HEAD

	// IO Methods
=======
	
>>>>>>> reworked aggregation
	public void write(String dir) throws IOException {
		for (AggregatedMetric metricData : this.getList()) {
			metricData.write(Dir.getMetricDataDir(dir, metricData.getName()));
		}
	}
<<<<<<< HEAD

	public static AggregatedMetricList read(String dir, boolean readValues)
			throws IOException {
		String[] metrics = Dir.getMetrics(dir);
		AggregatedMetricList list = new AggregatedMetricList(metrics.length);
		for (String metric : metrics) {
			list.add(AggregatedMetric.read(dir + metric + Dir.delimiter,
					Dir.getMetricName(metric), readValues));
		}
		return list;
	}
=======
	
>>>>>>> reworked aggregation
}
