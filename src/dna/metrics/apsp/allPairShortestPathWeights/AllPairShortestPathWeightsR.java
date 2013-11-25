package dna.metrics.apsp.allPairShortestPathWeights;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class AllPairShortestPathWeightsR extends
		AllPairShortestPathWeights {

	public AllPairShortestPathWeightsR() {
		super("AllPairShortestPathWeightsR",
				ApplicationType.Recomputation);
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
