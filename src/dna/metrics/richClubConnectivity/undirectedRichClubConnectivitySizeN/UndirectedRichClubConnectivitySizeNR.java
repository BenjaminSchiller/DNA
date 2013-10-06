package dna.metrics.richClubConnectivity.undirectedRichClubConnectivitySizeN;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class UndirectedRichClubConnectivitySizeNR extends
		UndirectedRichClubConnectivitySizeN {
	public UndirectedRichClubConnectivitySizeNR(int richClubSize) {
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
