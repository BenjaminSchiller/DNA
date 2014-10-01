package dna.depr.metrics.clusterCoefficient;

import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Recomputation of the directed clustering coefficient.
 * 
 * @author benni
 * 
 */
public class DirectedClusteringCoefficientR extends
		DirectedClusteringCoefficient {

	public DirectedClusteringCoefficientR() {
		super("DirectedClusteringCoefficientR", ApplicationType.Recomputation,
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