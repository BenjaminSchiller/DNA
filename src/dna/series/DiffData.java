package dna.series;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class DiffData {

	public DiffData(long timestamp) {
		this.timestamp = timestamp;
		this.values = new HashMap<String, Value>();
		this.generalRuntimes = new HashMap<String, RunTime>();
		this.metricRuntimes = new HashMap<String, RunTime>();
		this.metrics = new HashMap<String, MetricData>();
	}

	public DiffData(long timestamp, int sizeValues, int sizeGeneralRuntimes,
			int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.values = new HashMap<String, Value>(sizeValues);
		this.generalRuntimes = new HashMap<String, RunTime>(sizeGeneralRuntimes);
		this.metricRuntimes = new HashMap<String, RunTime>(sizeMetricRuntimes);
		this.metrics = new HashMap<String, MetricData>(sizeMetrics);
	}

	public DiffData(long timestamp, Value[] values, RunTime[] generalRuntimes,
			RunTime[] metricRuntimes, MetricData[] metrics) {
		this.timestamp = timestamp;
		this.values = new HashMap<String, Value>(values.length);
		for (Value value : values) {
			this.addValue(value);
		}
		this.generalRuntimes = new HashMap<String, RunTime>(
				generalRuntimes.length);
		for (RunTime runtime : generalRuntimes) {
			this.addGeneralRuntime(runtime);
		}
		this.metricRuntimes = new HashMap<String, RunTime>(
				metricRuntimes.length);
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

	private HashMap<String, Value> values;

	public Collection<Value> getValues() {
		return this.values.values();
	}

	public Value getValue(String name) {
		return this.values.get(name);
	}

	public void addValue(Value value) {
		this.values.put(value.getName(), value);
	}

	private HashMap<String, RunTime> generalRuntimes;

	public Collection<RunTime> getGeneralRuntimes() {
		return this.generalRuntimes.values();
	}

	public RunTime getGeneralRuntime(String name) {
		return this.generalRuntimes.get(name);
	}

	public void addGeneralRuntime(RunTime runtime) {
		this.generalRuntimes.put(runtime.getName(), runtime);
	}

	private HashMap<String, RunTime> metricRuntimes;

	public Collection<RunTime> getMetricRuntimes() {
		return this.metricRuntimes.values();
	}

	public RunTime getMetricRuntime(String name) {
		return this.metricRuntimes.get(name);
	}

	public void addMetricRuntime(RunTime runtime) {
		this.metricRuntimes.put(runtime.getName(), runtime);
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
		for(Value v : this.getValues()){
			v.write(dir + "_stats/");
		}
		for (RunTime rt : this.getGeneralRuntimes()) {
			rt.write(dir + "_runtime/");
		}
		for (RunTime rt : this.getMetricRuntimes()) {
			rt.write(dir + "_metrics/");
		}
		for (MetricData metricData : this.getMetrics()) {
			for (Value v : metricData.getValues()) {
				v.write(dir + metricData.getName() + "/");
			}
			for (Distribution d : metricData.getDistributions()) {
				d.write(dir + metricData.getName() + "/");
			}
		}
	}

	// TODO add reader
}
