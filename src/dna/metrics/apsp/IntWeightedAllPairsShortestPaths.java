package dna.metrics.apsp;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.weightsNew.IWeightedEdge;
import dna.graph.weightsNew.IntWeight;
import dna.updates.batch.Batch;

public abstract class IntWeightedAllPairsShortestPaths extends
		AllPairsShortestPaths {

	public IntWeightedAllPairsShortestPaths(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public boolean isApplicable(Graph g) {
		return IWeightedEdge.class.isAssignableFrom(g.getGraphDatastructures()
				.getEdgeType())
				&& IntWeight.class.isAssignableFrom(g.getGraphDatastructures()
						.getEdgeWeightType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return Node.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
