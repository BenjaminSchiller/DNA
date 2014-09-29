package dna.metricsNew.algorithms;

import dna.updates.update.NodeAddition;

public interface IBeforeNA extends IDynamicAlgorithm {
	public boolean applyBeforeUpdate(NodeAddition na);
}
