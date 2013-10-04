package dna.metrics.richClubConnectivity.directedRichClubConnectivityPerDegree;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class DirectedRichClubConnectivityPerDegreeR extends DirectedRichClubConnectivityPerDegree {
	public DirectedRichClubConnectivityPerDegreeR() {
		super("RCCPerDegreeComp", ApplicationType.Recomputation);
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
