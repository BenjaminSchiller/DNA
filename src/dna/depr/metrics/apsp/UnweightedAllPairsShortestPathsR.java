package dna.depr.metrics.apsp;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class UnweightedAllPairsShortestPathsR extends
		UnweightedAllPairsShortestPaths {

	public UnweightedAllPairsShortestPathsR() {
		super("UnweightedAllPairsShortestPathsR", ApplicationType.Recomputation);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		for (IElement n_ : this.g.getNodes()) {
			this.compute((Node) n_);
		}
		return true;
	}
}
