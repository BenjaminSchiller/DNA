package dna.metrics.weights;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.IEdge;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.graph.weights.intW.IntWeight;
import dna.metrics.IMetric;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.updates.batch.Batch;

public abstract class EdgeWeights extends Weights {

	public EdgeWeights(String name, double binSize) {
		super(name, binSize);
	}

	public boolean compute() {
		this.distr = new BinnedDoubleDistr("EdgeWeightsDistribution",
				this.binSize);
		for (IElement e : this.g.getEdges()) {
			this.distr.incr(this.getWeight((IEdge) e));
		}
		return true;
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof EdgeWeights;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& g.getGraphDatastructures().isEdgeWeightType(IntWeight.class,
						DoubleWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& b.getGraphDatastructures().isEdgeWeightType(IntWeight.class,
						DoubleWeight.class);
	}

	protected double getWeight(IEdge e, int index) {
		return this.getWeight(((IWeightedEdge) e).getWeight(), index);
	}

	protected double getWeight(IEdge e) {
		return this.getWeight(((IWeightedEdge) e).getWeight());
	}

}
