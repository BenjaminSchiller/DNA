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

=======
	
>>>>>>> reworked aggregation
	public void write(String dir) throws IOException {
		for (AggregatedMetric metricData : this.getList()) {
			metricData.write(Dir.getMetricDataDir(dir, metricData.getName()));
		}
	}
<<<<<<< HEAD

=======
	
>>>>>>> reworked aggregation
}
