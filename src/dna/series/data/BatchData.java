package dna.series.data;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.io.filesystem.Names;
import dna.series.lists.MetricDataList;
import dna.series.lists.RunTimeList;
import dna.series.lists.ValueList;
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
		Log.debug("writing DiffData for " + this.timestamp + " to " + dir);
		this.stats.write(dir, Files.getValuesFilename(Names.diffStats));
		this.generalRuntimes.write(dir,
				Files.getRuntimesFilename(Names.diffGeneralRuntimes));
		this.metricRuntimes.write(dir,
				Files.getRuntimesFilename(Names.diffMetricRuntimes));
		this.metrics.write(dir);
	}

	public static BatchData read(String dir, long timestamp,
			boolean readDistributionValues) throws IOException {
		ValueList values = ValueList.read(dir,
				Files.getValuesFilename(Names.diffStats));
		RunTimeList generalRuntimes = RunTimeList.read(dir,
				Files.getRuntimesFilename(Names.diffGeneralRuntimes));
		RunTimeList metricRuntimes = RunTimeList.read(dir,
				Files.getRuntimesFilename(Names.diffMetricRuntimes));
		MetricDataList metrics = MetricDataList.read(dir,
				readDistributionValues);
		return new BatchData(timestamp, values, generalRuntimes, metricRuntimes,
				metrics);
	}

}