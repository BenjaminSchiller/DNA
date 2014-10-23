package dna.depr.metrics.clusterCoefficient;

import dna.metrics.IMetric;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Recomputation of the undirected clustering coefficient.
 * 
 * @author benni
 * 
 */
public class UndirectedClusteringCoefficientR extends
		UndirectedClusteringCoefficient {

	public UndirectedClusteringCoefficientR() {
		super("UndirectedClusteringCoefficientR",
				ApplicationType.Recomputation, IMetric.MetricType.exact);
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
