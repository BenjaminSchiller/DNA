package dna.metrics.clustering.local;

import dna.metrics.algorithms.IRecomputation;

public class UndirectedLocalClusteringCoefficientR extends
		UndirectedLocalClusteringCoefficient implements IRecomputation {

	public UndirectedLocalClusteringCoefficientR(int... indexes) {
		super("UndirectedLocalClusteringCoefficientR", indexes);
	}

	@Override
	public boolean recompute() {
		for (int i = 0; i < this.indexes.length; i++) {
			if (this.g.isDirected()) {
				this.open[i] = this.computeOpenDirected(this.indexes[i]);
				this.closed[i] = this.computeClosedDirected(this.indexes[i]);
			} else {
				this.open[i] = this.computeOpenUndirected(this.indexes[i]);
				this.closed[i] = this.computeClosedUndirected(this.indexes[i]);
			}
		}
		return true;
	}

}
