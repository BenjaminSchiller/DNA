package dna.metrics.richClubConnectivity.undirectedRichClubConnectivityForOneDegree;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class UndirectedRichClubConnectivityForOneDegreeR extends
		UndirectedRichClubConnectivityForOneDegree {

	public UndirectedRichClubConnectivityForOneDegreeR(int minDegree) {
		super("RCCForOneDegreeRecomp", ApplicationType.Recomputation, minDegree);
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
