package dna.metrics.apsp;

import dna.graph.Graph;
import dna.updates.batch.Batch;

public abstract class UnweightedAllPairsShortestPaths extends
		AllPairsShortestPaths {

	public UnweightedAllPairsShortestPaths(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

}
