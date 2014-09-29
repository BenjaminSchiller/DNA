package dna.metricsNew.algorithms;

import dna.updates.update.EdgeRemoval;

public interface IAfterER extends IDynamicAlgorithm {
	public boolean applyAfterUpdate(EdgeRemoval er);
}
