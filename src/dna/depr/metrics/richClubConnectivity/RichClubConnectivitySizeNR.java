package dna.depr.metrics.richClubConnectivity;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * @author Jan Calculate the rich club connectivity value for a richClub with
 *         size @param RichClubSize
 * 
 */
public class RichClubConnectivitySizeNR extends RichClubConnectivitySizeN {
	public RichClubConnectivitySizeNR(int richClubSize) {
		super("RichClubConnectivitySizeNR", ApplicationType.Recomputation,
				richClubSize);
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
