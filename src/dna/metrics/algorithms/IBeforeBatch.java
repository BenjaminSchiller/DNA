package dna.metrics.algorithms;

import dna.updates.batch.Batch;

public interface IBeforeBatch extends IDynamicAlgorithm {
	public boolean applyBeforeBatch(Batch b);
}
