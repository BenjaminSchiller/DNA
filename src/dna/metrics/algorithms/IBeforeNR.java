package dna.metrics.algorithms;

import dna.updates.update.NodeRemoval;

public interface IBeforeNR extends IDynamicAlgorithm {
	public boolean applyBeforeUpdate(NodeRemoval nr);
}
