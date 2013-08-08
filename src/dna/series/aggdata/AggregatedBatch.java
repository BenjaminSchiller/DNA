package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.io.filesystem.Names;
import dna.util.Log;

/**
 * An AggregatedBatch contains aggregated values of a batch.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedBatch {

	// member variables
	private long timestamp;
	private AggregatedValueList stats;
	private AggregatedRunTimeList generalRuntimes;
	private AggregatedRunTimeList metricRuntimes;
	private AggregatedMetricList metrics;
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	// constructors
	public AggregatedBatch(long timestamp) {
		this.timestamp = timestamp;
		this.stats = new AggregatedValueList();
		this.generalRuntimes = new AggregatedRunTimeList();
		this.metricRuntimes = new AggregatedRunTimeList();
		this.metrics = new AggregatedMetricList();
	}
<<<<<<< HEAD
	
	public AggregatedBatch(long timestamp, int sizeValues, int sizeGeneralRuntimes,
			int sizeMetricRuntimes, int sizeMetrics) {
=======

	public AggregatedBatch(long timestamp, int sizeValues,
			int sizeGeneralRuntimes, int sizeMetricRuntimes, int sizeMetrics) {
>>>>>>> remotes/beniMaster/master
		this.timestamp = timestamp;
		this.stats = new AggregatedValueList(sizeValues);
		this.generalRuntimes = new AggregatedRunTimeList(sizeGeneralRuntimes);
		this.metricRuntimes = new AggregatedRunTimeList(sizeMetricRuntimes);
		this.metrics = new AggregatedMetricList(sizeMetrics);
	}

<<<<<<< HEAD
	public AggregatedBatch(long timestamp, AggregatedValueList values, 
			AggregatedRunTimeList generalRuntimes, AggregatedRunTimeList metricRuntimes,
			AggregatedMetricList metrics) {
=======
	public AggregatedBatch(long timestamp, AggregatedValueList values,
			AggregatedRunTimeList generalRuntimes,
			AggregatedRunTimeList metricRuntimes, AggregatedMetricList metrics) {
>>>>>>> remotes/beniMaster/master
		this.timestamp = timestamp;
		this.stats = values;
		this.generalRuntimes = generalRuntimes;
		this.metricRuntimes = metricRuntimes;
		this.metrics = metrics;
	}
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	// methods
	public long getTimestamp() {
		return this.timestamp;
	}
<<<<<<< HEAD
	
	public AggregatedValueList getValues() {
		return this.stats;
	}
	
	public AggregatedRunTimeList getGeneralRuntimes() {
		return this.generalRuntimes;
	}
	
	public AggregatedRunTimeList getMetricRuntimes() {
		return this.metricRuntimes;
	}
	
	public AggregatedMetricList getMetrics() {
		return this.metrics;
	}
	
=======

	public AggregatedValueList getValues() {
		return this.stats;
	}

	public AggregatedRunTimeList getGeneralRuntimes() {
		return this.generalRuntimes;
	}

	public AggregatedRunTimeList getMetricRuntimes() {
		return this.metricRuntimes;
	}

	public AggregatedMetricList getMetrics() {
		return this.metrics;
	}

>>>>>>> remotes/beniMaster/master
	// IO methods
	public void write(String dir) throws IOException {
		Log.debug("writing AggregatedBatchfor " + this.timestamp + " to " + dir);
		this.stats.write(dir, Files.getValuesFilename(Names.batchStats));
		this.generalRuntimes.write(dir,
				Files.getRuntimesFilename(Names.batchGeneralRuntimes));
		this.metricRuntimes.write(dir,
				Files.getRuntimesFilename(Names.batchMetricRuntimes));
		this.metrics.write(dir);
	}
}
