package dna.metrics.connectedComponents;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class CCDirectedComp extends CCDirected {

	public CCDirectedComp() {
		super("CCDirectedComp", ApplicationType.Recomputation);
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
