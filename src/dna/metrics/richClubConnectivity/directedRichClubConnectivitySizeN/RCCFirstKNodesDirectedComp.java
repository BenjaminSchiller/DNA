package dna.metrics.richClubConnectivity.directedRichClubConnectivitySizeN;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class RCCFirstKNodesDirectedComp extends RCCFirstKNodesDirected {
	public RCCFirstKNodesDirectedComp() {
		super("RCCFirstKNodesComp", ApplicationType.Recomputation);
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
