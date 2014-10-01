package dna.depr.metrics.weights;

import dna.depr.metrics.Metric;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.INode;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;

public abstract class NodeWeights extends Weights {

	public NodeWeights(String name, ApplicationType type,
			IMetricNew.MetricType metricType, double binSize) {
		super(name, type, metricType, binSize);
	}

	@Override
	public boolean compute() {
		for (IElement n : this.g.getNodes()) {
			this.distr.incr(this.getWeight((INode) n));
		}
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& g.getGraphDatastructures().isNodeWeightType(IntWeight.class,
						DoubleWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& b.getGraphDatastructures().isNodeWeightType(IntWeight.class,
						DoubleWeight.class);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof NodeWeights;
	}

	protected double getWeight(INode n) {
		return this.getWeight(((IWeightedNode) n).getWeight());
	}

}
