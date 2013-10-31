package dna.metrics.apsp.allPairShortestPathComplete;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class DirectedAllPairShortestPathCompleteR extends
		DirectedAllPairShortestPathComplete {

	public DirectedAllPairShortestPathCompleteR() {
		super("APSP Complete Comp", ApplicationType.Recomputation);

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
