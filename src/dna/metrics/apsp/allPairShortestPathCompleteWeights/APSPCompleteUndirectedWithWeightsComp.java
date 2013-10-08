package dna.metrics.apsp.allPairShortestPathCompleteWeights;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class APSPCompleteUndirectedWithWeightsComp extends
		APSPCompleteUndirectedWithWeights {

	public APSPCompleteUndirectedWithWeightsComp() {
		super("APSP Undirected wiht Weights Comp",
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
