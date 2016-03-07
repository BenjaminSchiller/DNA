package dna.metrics.clustering;

import dna.metrics.algorithms.IRecomputation;

public class DirectedClusteringCoefficientR extends
		DirectedClusteringCoefficient implements IRecomputation {

	public DirectedClusteringCoefficientR() {
		super("DirectedClusteringCoefficientR");
	}

	public DirectedClusteringCoefficientR(String[] nodeTypes) {
		super("DirectedClusteringCoefficientR", nodeTypes);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
