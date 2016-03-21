package dna.metrics.weights;

import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.nodes.INode;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.graph.weights.intW.IntWeight;
import dna.metrics.IMetric;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.updates.batch.Batch;

public abstract class NodeWeights extends Weights {

	public NodeWeights(String name, double binSize) {
		super(name, binSize);
	}

	public boolean compute() {
		this.distr = new BinnedDoubleDistr("NodeWeightsDistribution",
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
	public boolean isApplicable(IGraph g) {
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

	protected double getWeight(INode n) {
		return this.getWeight(((IWeightedNode) n).getWeight());
	}

}
