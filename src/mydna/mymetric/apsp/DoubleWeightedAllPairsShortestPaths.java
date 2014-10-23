package mydna.mymetric.apsp;

import dna.graph.Graph;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;

public abstract class DoubleWeightedAllPairsShortestPaths extends
		AllPairsShortestPaths {

	public DoubleWeightedAllPairsShortestPaths(String name) {
		super(name);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof DoubleWeightedAllPairsShortestPaths;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& g.getGraphDatastructures().isNodeWeightType(Double3dWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& b.getGraphDatastructures().isNodeWeightType(Double3dWeight.class);
	}

}
