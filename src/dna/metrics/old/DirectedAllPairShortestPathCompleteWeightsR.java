package dna.metrics.old;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

@SuppressWarnings("rawtypes")
public class DirectedAllPairShortestPathCompleteWeightsR extends
		DirectedAllPairShortestPathCompleteWeights {

	public DirectedAllPairShortestPathCompleteWeightsR() {
		super("APSP Directed wiht Weights Comp", ApplicationType.Recomputation);
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
