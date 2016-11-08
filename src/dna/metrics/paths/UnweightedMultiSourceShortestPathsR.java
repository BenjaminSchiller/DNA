package dna.metrics.paths;

import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distr.BinnedIntDistr;

public class UnweightedMultiSourceShortestPathsR extends
		UnweightedMultiSourceShortestPaths implements IRecomputation {

	public UnweightedMultiSourceShortestPathsR(int sources) {
		super("UnweightedMultiSourceShortestPathsR", sources);
	}

	public UnweightedMultiSourceShortestPathsR(int sources, String[] nodeTypes) {
		super("UnweightedMultiSourceShortestPathsR", sources, nodeTypes);
	}

	@Override
	public boolean recompute() {
		this.apsp = new BinnedIntDistr("APSP");
		for (Node n : this.getSources()) {
			this.compute(n);
		}
		return true;
	}

}