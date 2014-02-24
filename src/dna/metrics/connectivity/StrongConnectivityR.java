package dna.metrics.connectivity;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Recomputation of weak connectivity.
 * 
 * @author benni
 * 
 */
public class StrongConnectivityR extends StrongConnectivity {

	public StrongConnectivityR() {
		super("StrongConnectivityR", ApplicationType.Recomputation,
				MetricType.exact);
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
