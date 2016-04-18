package dna.metrics.paths.unweighted;

import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distr.BinnedIntDistr;

public class UnweightedMultiSourceShortestPathsR extends
		UnweightedMultiSourceShortestPaths implements IRecomputation {

	public UnweightedMultiSourceShortestPathsR(int sources) {
		super("UnweightedMultiSourceShortestPathsR", sources);
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
