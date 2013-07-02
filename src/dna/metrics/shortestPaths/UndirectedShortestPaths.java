package dna.metrics.shortestPaths;

import dna.graph.Graph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class UndirectedShortestPaths extends ShortestPaths {

	public UndirectedShortestPaths(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof UndirectedShortestPaths;
	}

}
