package dna.metrics.algorithms;

import dna.updates.update.NodeWeight;

public interface IAfterNW extends IDynamicAlgorithm {
	public boolean applyAfterUpdate(NodeWeight nw);
}
