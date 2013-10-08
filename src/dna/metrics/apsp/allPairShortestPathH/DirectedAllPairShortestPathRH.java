package dna.metrics.apsp.allPairShortestPathH;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class DirectedAllPairShortestPathRH extends DirectedAllPairShortestPath {

	public DirectedAllPairShortestPathRH() {
		super("APSP directed Comp", ApplicationType.Recomputation);
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

}
