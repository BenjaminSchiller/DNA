package dna.metricsNew.algorithms;

import dna.updates.update.NodeRemoval;

public interface IAfterNR extends IDynamicAlgorithm {
	public boolean applyAfterUpdate(NodeRemoval nr);
}
