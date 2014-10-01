package dna.depr.metrics.weights;

import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class NodeWeightsR extends NodeWeights {

	public NodeWeightsR(double binSize) {
		super("NodeWeightsR", ApplicationType.Recomputation, IMetricNew.MetricType.exact,
				binSize);
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
