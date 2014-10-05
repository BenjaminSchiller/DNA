package dna.depr.metrics.shortestPaths;

import dna.depr.metrics.MetricOld;
import dna.graph.Graph;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;

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
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof UndirectedShortestPaths;
	}

}
