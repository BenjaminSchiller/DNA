package dna.depr.metrics.richClubConnectivity;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Calculates the rich club connectivity values for all existing degrees n, with
 * Node n ∈ richclub if degree > n
 * 
 */
public class RichClubConnectivityPerDegreeR extends
		RichClubConnectivityPerDegree {
	public RichClubConnectivityPerDegreeR() {
		super("RichClubConnectivityPerDegreeR", ApplicationType.Recomputation);
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
