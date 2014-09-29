package dna.metricsNew.algorithms;

import dna.updates.update.EdgeAddition;

public interface IBeforeEA extends IDynamicAlgorithm {
	public boolean applyBeforeUpdate(EdgeAddition ea);
}
