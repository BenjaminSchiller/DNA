package dna.metricsNew.algorithms;

import dna.updates.update.NodeAddition;

public interface IAfterNA extends IDynamicAlgorithm {
	public boolean applyAfterUpdate(NodeAddition na);
}
