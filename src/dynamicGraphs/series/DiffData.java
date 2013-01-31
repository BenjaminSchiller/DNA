package dynamicGraphs.series;

import java.util.Collection;
import java.util.HashMap;

public class DiffData {

	public DiffData(long timestamp) {
		this.timestamp = timestamp;
		this.generalRuntimes = new HashMap<String, RunTime>();
		this.metricRuntimes = new HashMap<String, RunTime>();
		this.metrics = new HashMap<String, MetricData>();
	}

	public DiffData(long timestamp, int sizeGeneralRuntimes,
			int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.generalRuntimes = new HashMap<String, RunTime>(sizeGeneralRuntimes);
		this.metricRuntimes = new HashMap<String, RunTime>(sizeMetricRuntimes);
		this.metrics = new HashMap<String, MetricData>(sizeMetrics);
	}

	public DiffData(long timestamp, RunTime[] generalRuntimes,
			RunTime[] metricRuntimes, MetricData[] metrics) {
		this.timestamp = timestamp;
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
}
