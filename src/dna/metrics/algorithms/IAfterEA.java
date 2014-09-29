package dna.metrics.algorithms;

import dna.updates.update.EdgeAddition;

public interface IAfterEA extends IDynamicAlgorithm {
	public boolean applyAfterUpdate(EdgeAddition ea);
}
