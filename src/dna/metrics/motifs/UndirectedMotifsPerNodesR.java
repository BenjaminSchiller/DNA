package dna.metrics.motifs;

import dna.metrics.algorithms.IRecomputation;

public class UndirectedMotifsPerNodesR extends UndirectedMotifsPerNodes implements
		IRecomputation {

	public UndirectedMotifsPerNodesR(int... nodeIndexes) {
		super("UndirectedMotifsPerNodesR", nodeIndexes);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
