package dna.metrics.weights;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.INode;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.IMetric;
import dna.series.data.BinnedDistributionInt;
import dna.updates.batch.Batch;

public abstract class NodeWeights extends Weights {

	public NodeWeights(String name, double binSize) {
		super(name, binSize);
	}

	public boolean compute() {
		this.distr = new BinnedDistributionInt("NodeWeightsDistribution",
				this.binSize);
		for (IElement n : this.g.getNodes()) {
			this.distr.incr(this.getWeight((INode) n));
		}
		return true;
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof NodeWeights;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& g.getGraphDatastructures().isNodeWeightType(IntWeight.class,
						DoubleWeight.class,Double3dWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& b.getGraphDatastructures().isNodeWeightType(IntWeight.class,
						DoubleWeight.class,Double3dWeight.class);
	}

	protected double getWeight(INode n) {
		return this.getWeight(((IWeightedNode) n).getWeight());
	}

}
