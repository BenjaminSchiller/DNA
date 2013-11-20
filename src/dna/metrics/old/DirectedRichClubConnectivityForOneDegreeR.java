package dna.metrics.old;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class DirectedRichClubConnectivityForOneDegreeR extends
		DirectedRichClubConnectivityForOneDegree {

	public DirectedRichClubConnectivityForOneDegreeR(int minDegree) {
		super("RCCOneDegreeComp", ApplicationType.Recomputation, minDegree);
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
