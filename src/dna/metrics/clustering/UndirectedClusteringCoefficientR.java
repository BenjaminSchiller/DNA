package dna.metrics.clustering;

import dna.metrics.algorithms.IRecomputation;

public class UndirectedClusteringCoefficientR extends
		UndirectedClusteringCoefficient implements IRecomputation, Cloneable {

	public UndirectedClusteringCoefficientR() {
		super("UndirectedClusteringCoefficientR");
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

	@Override
	public UndirectedClusteringCoefficientR clone() {
		return new UndirectedClusteringCoefficientR();
	}

}
