package dna.metrics.apsp;

import dna.graph.Graph;
import dna.graph.edges.DirectedIntWeightedEdge;
import dna.graph.edges.UndirectedIntWeightedEdge;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;

public abstract class IntWeightedAllPairsShortestPaths extends AllPairsShortestPaths {

	public IntWeightedAllPairsShortestPaths(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedIntWeightedEdge.class.isAssignableFrom(g
				.getGraphDatastructures().getEdgeType())
				|| UndirectedIntWeightedEdge.class.isAssignableFrom(g
						.getGraphDatastructures().getEdgeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return Node.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
