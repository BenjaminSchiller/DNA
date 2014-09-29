package dna.metricsNew.algorithms;

import dna.updates.batch.Batch;

public interface IAfterBatch extends IDynamicAlgorithm {
	public boolean applyAfterBatch(Batch b);
}
