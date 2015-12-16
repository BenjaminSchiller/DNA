package dna.metrics.clustering.local;

import dna.metrics.algorithms.IRecomputation;

public class DirectedLocalClusteringCoefficientR extends
		DirectedLocalClusteringCoefficient implements IRecomputation {

	public DirectedLocalClusteringCoefficientR(int... indexes) {
		super("DirectedLocalClusteringCoefficientR", indexes);
	}

	@Override
	public boolean recompute() {
		for (int i = 0; i < this.indexes.length; i++) {
			this.open[i] = this.computeOpen(this.indexes[i]);
			this.closed[i] = this.computeClosed(this.indexes[i]);
		}
		return true;
	}

}
