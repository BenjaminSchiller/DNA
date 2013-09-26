package dna.metrics.apsp;

import dna.updates.Batch;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class APSPCompleteUndirectedWithWeightsComp extends
		APSPCompleteUndirectedWithWeights {

	public APSPCompleteUndirectedWithWeightsComp(String name,
			ApplicationType type) {
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
