package dna.metrics.algorithms;

import dna.updates.update.EdgeWeight;

public interface IAfterEW extends IDynamicAlgorithm {
	public boolean applyAfterUpdate(EdgeWeight ew);
}
