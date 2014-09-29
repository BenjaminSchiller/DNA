package dna.depr.metrics.connectivity;

import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Recomputation of weak connectivity.
 * 
 * @author benni
 * 
 */
public class WeakConnectivityR extends WeakConnectivity {

	public WeakConnectivityR() {
		super("WeakConnectivityR", ApplicationType.Recomputation,
				IMetricNew.MetricType.exact);
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
