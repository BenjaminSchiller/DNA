package dna.metrics.richClubConnectivity.richClubConnectivityInterval;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Calculates the rich club connectivity values for all richClubs with size n*
 * interval
 * 
 */
public class RichClubConnectivityIntervalR extends RichClubConnectivityInterval {
	public RichClubConnectivityIntervalR(int interval) {
		super("RichClubConnectivityIntervalR", ApplicationType.Recomputation,
				interval);
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
