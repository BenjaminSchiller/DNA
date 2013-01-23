package dynamicGraphs.util;

import dynamicGraphs.metrics.Metric;

public class MetricStats extends Stats {
	public MetricStats(Metric m) {
		super(m.toString());
		this.m = m;
	}

	private Metric m;

	public Metric getMetric() {
		return this.m;
	}

}
