package dna.metrics.streaM_k;

import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;

public class AdjacencyMatrix_kR extends AdjacencyMatrix_k implements
		IRecomputation {

	public AdjacencyMatrix_kR(int... nodes) {
		super("AdjacencyMatrix_kR", nodes);
	}

	@Override
	public boolean recompute() {
		this.key = 0;
		for (int i = 0; i < this.nodes.length; i++) {
			Node a = this.g.getNode(this.nodes[i]);
			for (int j = i + 1; j < this.nodes.length; j++) {
				if (j > 1) {
					key = key << 1;
				}
				if (a.hasEdge(a.getIndex(), nodes[j])) {
					key += 1;
				}
			}
		}

		return true;
	}
}
