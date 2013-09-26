package dna.series.data;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.lists.MetricDataList;
import dna.series.lists.RunTimeList;
import dna.series.lists.ValueList;
import dna.util.Config;
import dna.util.Log;

public class BatchData {

	public BatchData(long timestamp) {
		this.timestamp = timestamp;
		this.stats = new ValueList();
		this.generalRuntimes = new RunTimeList();
		this.metricRuntimes = new RunTimeList();
		this.metrics = new MetricDataList();
	}

	public BatchData(long timestamp, int sizeValues, int sizeGeneralRuntimes,
			int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.stats = new ValueList(sizeValues);
		this.generalRuntimes = new RunTimeList(sizeGeneralRuntimes);
		this.metricRuntimes = new RunTimeList(sizeMetricRuntimes);
		this.metrics = new MetricDataList(sizeMetrics);
	}

	public BatchData(long timestamp, ValueList values,
			RunTimeList generalRuntimes, RunTimeList metricRuntimes,
			MetricDataList metrics) {
		this.timestamp = timestamp;
		this.stats = values;
		this.generalRuntimes = generalRuntimes;
		this.metricRuntimes = metricRuntimes;
		this.metrics = metrics;
	}

	private long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	private ValueList stats;

	public ValueList getValues() {
		return this.stats;
	}

	private RunTimeList generalRuntimes;

	public RunTimeList getGeneralRuntimes() {
		return this.generalRuntimes;
	}

	private RunTimeList metricRuntimes;

	public RunTimeList getMetricRuntimes() {
		return this.metricRuntimes;
	}

	private MetricDataList metrics;

	public MetricDataList getMetrics() {
		return this.metrics;
	}

	public void write(String dir) throws IOException {
		Log.debug("writing BatchData for " + this.timestamp + " to " + dir);
		this.stats.write(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));
		this.generalRuntimes
				.write(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		this.metricRuntimes.write(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
		this.metrics.write(dir);
	}

	public static BatchData read(String dir, long timestamp, boolean readValues)
			throws IOException {
		ValueList values = ValueList.read(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));
		RunTimeList generalRuntimes = RunTimeList
				.read(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		RunTimeList metricRuntimes = RunTimeList.read(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
		MetricDataList metrics = MetricDataList.read(dir, readValues);
		return new BatchData(timestamp, values, generalRuntimes,
				metricRuntimes, metrics);
	}
	
	
	/**
	 * This method tests if two different BatchData objects can be aggregated.
	 * Checks:		- same timestamp
	 * 				- same amount of metrics
	 * 				- same metrics (uses MetricData.symeType())
	 * 
	 * @author Rwilmes
	 * @date 24.06.2013
	 */
	public static boolean sameType(BatchData b1, BatchData b2) {
		if(b1.getTimestamp() != b2.getTimestamp()) {
			Log.warn("different timestamps on batch " + b1.getTimestamp() + " and batch " + b2.getTimestamp());
			return false;
		}
		
		MetricDataList list1 = b1.getMetrics();
		MetricDataList list2 = b2.getMetrics();
		
		if(list1.size() != list2.size()) {
			Log.warn("different amount of metrics on batch " + b1.getTimestamp() + " and batch " + b2.getTimestamp());
			return false;
		}
		for(String k : list1.getNames()) {
			if(!MetricData.sameType(list1.get(k), list2.get(k))) {
				Log.warn("different metrics on batch " + b1.getTimestamp() + " and batch " + b2.getTimestamp());
				return false;
			}
		}

		return true;
	}

	/**
	 * This method tests if two different BatchData objects can be aggregated.
	 * Checks: - same timestamp - same amount of metrics - same metrics (uses
	 * MetricData.sameType())
	 * 
	 * @author Rwilmes
	 * @date 24.06.2013
	 */
	public static boolean isSameType(BatchData b1, BatchData b2) {
		if (b1.getTimestamp() != b2.getTimestamp()) {
			Log.warn("different timestamps on batch " + b1.getTimestamp()
					+ " and batch " + b2.getTimestamp());
			return false;
		}

		MetricDataList list1 = b1.getMetrics();
		MetricDataList list2 = b2.getMetrics();

		if (list1.size() != list2.size()) {
			Log.warn("different amount of metrics on batch "
					+ b1.getTimestamp() + " and batch " + b2.getTimestamp());
			return false;
		}
		for (String k : list1.getNames()) {
			if (!MetricData.isSameType(list1.get(k), list2.get(k))) {
				Log.warn("different metrics on batch " + b1.getTimestamp()
						+ " and batch " + b2.getTimestamp());
				return false;
			}
		}

		return true;
	}

}