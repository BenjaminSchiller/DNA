package dna.metrics.clustering;

import dna.metrics.algorithms.IRecomputation;

public class UndirectedClusteringCoefficientR extends
		UndirectedClusteringCoefficient implements IRecomputation {

	public UndirectedClusteringCoefficientR() {
		super("UndirectedClusteringCoefficientR");
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
