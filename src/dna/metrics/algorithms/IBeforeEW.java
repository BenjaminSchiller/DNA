package dna.metrics.algorithms;

import dna.updates.update.EdgeWeight;

public interface IBeforeEW extends IDynamicAlgorithm {
	public boolean applyBeforeUpdate(EdgeWeight ew);
}
