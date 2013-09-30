package dna.metrics.apsp;

import dna.updates.Batch;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class APSPCompleteDirectedComp extends APSPCompleteDirected {

	public APSPCompleteDirectedComp() {
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
