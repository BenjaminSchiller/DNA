package dna.metrics.algorithms;

import dna.updates.update.EdgeRemoval;

public interface IBeforeER extends IDynamicAlgorithm {
	public boolean applyBeforeUpdate(EdgeRemoval er);
}
