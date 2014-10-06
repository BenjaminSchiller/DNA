package dna.metrics;

import dna.graph.Graph;
import dna.series.data.MetricData;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class Metric extends ParameterList implements IMetric {

	public Metric(String name, Parameter... p) {
		super(name, p);
		this.metricType = MetricType.exact;
	}

	public Metric(String name, Parameter[] params, Parameter... p) {
		super(name, combine(params, p));
		this.metricType = MetricType.exact;
	}

	public Metric(String name, IMetric.MetricType metricType, Parameter... p) {
		super(name, p);
		this.metricType = metricType;
	}

	public Metric(String name, IMetric.MetricType metricType,
			Parameter[] params, Parameter... p) {
		super(name, combine(params, p));
		this.metricType = metricType;
	}

	protected static Parameter[] combine(Parameter[] p1, Parameter[] p2) {
		if (p2.length == 0) {
			return p1;
		} else if (p1.length == 0) {
			return p2;
		}
		Parameter[] p = new Parameter[p1.length + p2.length];
		for (int i = 0; i < p1.length; i++) {
			p[i] = p1[i];
		}
		for (int i = 0; i < p2.length; i++) {
			p[p1.length + i] = p2[i];
		}
		return p;
	}

	protected Graph g;

	public Graph getGraph() {
		return this.g;
	}

	public void setGraph(Graph g) {
		this.g = g;
	}

	protected IMetric.MetricType metricType;

	public IMetric.MetricType getMetricType() {
		return this.metricType;
	}

	/**
	 * 
	 * @return all data computed by this metric
	 */
	public MetricData getData() {
		// TODO remove metric type from all components
		return new MetricData(this.getName(), this.getMetricType(),
				this.getValues(), this.getDistributions(),
				this.getNodeValueLists(), this.getNodeNodeValueLists());
	}

}
