package dna.metrics.old;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class DirectedRichClubConnectivitySizeNR extends
		DirectedRichClubConnectivitySizeN {
	public DirectedRichClubConnectivitySizeNR(int richClubSize) {
		super("RCCSizeNRecomp", ApplicationType.Recomputation, richClubSize);
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
