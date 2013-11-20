package dna.metrics.richClubConnectivity.RichClubConnectivitySizeN;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class RichClubConnectivitySizeNR extends
		RichClubConnectivitySizeN {
	public RichClubConnectivitySizeNR(int richClubSize) {
		super("RCCFirstKNodesComp", ApplicationType.Recomputation, richClubSize);
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
