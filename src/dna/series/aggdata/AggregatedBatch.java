package dna.series.aggdata;

import java.io.IOException;

import dna.io.ZipWriter;
import dna.io.filesystem.Files;
import dna.series.SeriesGeneration;
import dna.util.Config;
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

	// constructors
	public AggregatedBatch(long timestamp) {
		this.timestamp = timestamp;
		this.stats = new AggregatedValueList();
		this.generalRuntimes = new AggregatedRunTimeList();
		this.metricRuntimes = new AggregatedRunTimeList();
		this.metrics = new AggregatedMetricList();
	}

	public AggregatedBatch(long timestamp, int sizeValues,
			int sizeGeneralRuntimes, int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.stats = new AggregatedValueList(sizeValues);
		this.generalRuntimes = new AggregatedRunTimeList(sizeGeneralRuntimes);
		this.metricRuntimes = new AggregatedRunTimeList(sizeMetricRuntimes);
		this.metrics = new AggregatedMetricList(sizeMetrics);
	}

	public AggregatedBatch(long timestamp, AggregatedValueList values,
			AggregatedRunTimeList generalRuntimes,
			AggregatedRunTimeList metricRuntimes, AggregatedMetricList metrics) {
		this.timestamp = timestamp;
		this.stats = values;
		this.generalRuntimes = generalRuntimes;
		this.metricRuntimes = metricRuntimes;
		this.metrics = metrics;
	}

	// methods
	public long getTimestamp() {
		return this.timestamp;
	}

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

	// IO methods
	public void write(String dir) throws IOException {
		Log.debug("writing AggregatedBatch for " + this.timestamp + " to "
				+ dir);
		this.stats.write(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));
		this.generalRuntimes
				.write(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		this.metricRuntimes.write(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
		this.metrics.write(dir);
	}

	public void writeSingleFile(String fsDir, long timestamp, String dir)
			throws Throwable {
		SeriesGeneration.fs = ZipWriter.createBatchFileSystem(fsDir, timestamp);
		this.write(dir);
		SeriesGeneration.fs.close();
		SeriesGeneration.fs = null;
	}

	public static AggregatedBatch read(String dir, long timestamp,
			boolean readValues) throws IOException {
		AggregatedValueList values = AggregatedValueList.read(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")), readValues);
		AggregatedRunTimeList generalRuntimes = AggregatedRunTimeList
				.read(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")), readValues);
		AggregatedRunTimeList metricRuntimes = AggregatedRunTimeList.read(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")),
				readValues);
		AggregatedMetricList metrics = AggregatedMetricList.read(dir,
				readValues);
		return new AggregatedBatch(timestamp, values, generalRuntimes,
				metricRuntimes, metrics);
	}
}
