package dna.metrics;

import com.google.common.collect.Iterables;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.weights.ITypedWeight;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.NodeTypeFilter;
import dna.series.data.MetricData;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class Metric extends ParameterList implements IMetric {

	protected String[] nodeTypes;
	protected NodeTypeFilter nodeTypeFilter;

	public Metric(String name, IMetric.MetricType metricType,
			String[] nodeTypes, Parameter... p) {
		super(name, p);
		this.metricType = metricType;
		this.nodeTypes = nodeTypes;
		if (this.nodeTypes.length > 0
				&& this.g.getGraphDatastructures().isNodeType(
						IWeightedNode.class)
				&& this.g.getGraphDatastructures().isNodeWeightType(
						ITypedWeight.class)) {
			this.nodeTypeFilter = new NodeTypeFilter(this.nodeTypes);
		} else {
			this.nodeTypeFilter = null;
		}
	}

	public Metric(String name, IMetric.MetricType metricType, Parameter... p) {
		this(name, metricType, new String[0], p);
	}

	protected Iterable<IElement> getNodesOfAssignedTypes() {
		if (this.nodeTypeFilter != null) {
			return Iterables.filter(this.g.getNodes(), this.nodeTypeFilter);
		} else {
			return this.g.getNodes();
		}
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

	@Override
	public boolean reset() {
		return true;
	}

}
