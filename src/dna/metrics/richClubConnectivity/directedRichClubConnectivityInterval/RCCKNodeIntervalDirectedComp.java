package dna.metrics.richClubConnectivity.directedRichClubConnectivityInterval;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class RCCKNodeIntervalDirectedComp extends RCCKNodeIntervalDirected {
	public RCCKNodeIntervalDirectedComp() {
		super("RCCKNodeIntervalComp", ApplicationType.Recomputation);
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
