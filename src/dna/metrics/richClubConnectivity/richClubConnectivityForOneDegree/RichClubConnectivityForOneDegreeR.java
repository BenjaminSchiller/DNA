package dna.metrics.richClubConnectivity.richClubConnectivityForOneDegree;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Calculates the rich club connectivity value for richClub consiting of nodes
 * with degree >= minDegree
 * 
 * 
 */
public class RichClubConnectivityForOneDegreeR extends
		RichClubConnectivityForOneDegree {

	public RichClubConnectivityForOneDegreeR(int minDegree) {
		super("RichClubConnectivityForOneDegreeR",
				ApplicationType.Recomputation, minDegree);
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
