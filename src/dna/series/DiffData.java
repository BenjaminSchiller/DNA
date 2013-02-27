package dna.series;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import dna.settings.Filenames;
import dna.util.Log;

public class DiffData {

	public DiffData(long timestamp) {
		this.timestamp = timestamp;
		this.values = new ValueList();
		this.generalRuntimes = new RunTimeList();
		this.metricRuntimes = new RunTimeList();
		this.metrics = new HashMap<String, MetricData>();
	}

	public DiffData(long timestamp, int sizeValues, int sizeGeneralRuntimes,
			int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.values = new ValueList(sizeValues);
		this.generalRuntimes = new RunTimeList(sizeGeneralRuntimes);
		this.metricRuntimes = new RunTimeList(sizeMetricRuntimes);
		this.metrics = new HashMap<String, MetricData>(sizeMetrics);
	}

	public DiffData(long timestamp, Value[] values, RunTime[] generalRuntimes,
			RunTime[] metricRuntimes, MetricData[] metrics) {
		this.timestamp = timestamp;
		this.values = new ValueList(values.length);
		for (Value value : values) {
			this.addValue(value);
		}
		this.generalRuntimes = new RunTimeList(generalRuntimes.length);
		for (RunTime runtime : generalRuntimes) {
			this.addGeneralRuntime(runtime);
		}
		this.metricRuntimes = new RunTimeList(metricRuntimes.length);
		for (RunTime runtime : metricRuntimes) {
			this.addMetricRuntime(runtime);
		}
		this.metrics = new HashMap<String, MetricData>(metrics.length);
		for (MetricData metric : metrics) {
			this.addMetric(metric);
		}
	}

	private long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	private ValueList values;

	public Collection<Value> getValues() {
		return this.values.getList();
	}

	public Value getValue(String name) {
		return this.values.get(name);
	}

	public void addValue(Value value) {
		this.values.add(value);
	}

	private RunTimeList generalRuntimes;

	public Collection<RunTime> getGeneralRuntimes() {
		return this.generalRuntimes.getList();
	}

	public RunTime getGeneralRuntime(String name) {
		return this.generalRuntimes.get(name);
	}

	public void addGeneralRuntime(RunTime runtime) {
		this.generalRuntimes.add(runtime);
	}

	private RunTimeList metricRuntimes;

	public Collection<RunTime> getMetricRuntimes() {
		return this.metricRuntimes.getList();
	}

	public RunTime getMetricRuntime(String name) {
		return this.metricRuntimes.get(name);
	}

	public void addMetricRuntime(RunTime runtime) {
		this.metricRuntimes.add(runtime);
	}

	private HashMap<String, MetricData> metrics;

	public Collection<MetricData> getMetrics() {
		return this.metrics.values();
	}

	public MetricData getMetric(String name) {
		return this.metrics.get(name);
	}

	public void addMetric(MetricData metric) {
		this.metrics.put(metric.getName(), metric);
	}

	public void write(String dir) throws IOException {
		Log.debug("writing DiffData for " + this.timestamp + " to " + dir);

		this.values.write(dir, Filenames.stats);
		this.generalRuntimes.write(dir, Filenames.generalRuntime);
		this.metricRuntimes.write(dir, Filenames.metricRuntime);

		for (MetricData metricData : this.getMetrics()) {
			// TODO adapt MetricData to also use ValueList
			// for (Value v : metricData.getValues()) {
			// v.write(dir + metricData.getName() + "/");
			// }
			for (Distribution d : metricData.getDistributions()) {
				// d.write(dir + metricData.getName() + "/");
			}
		}
	}

	// TODO add reader
}
