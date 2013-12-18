package dna.metrics.motifs.undirectedMotifs.exeptions;

import dna.graph.edges.UndirectedEdge;
import dna.metrics.motifs.undirectedMotifs.UndirectedMotif;

public abstract class UndirectedMotifException extends Exception {
	private static final long serialVersionUID = 8765421972727924545L;

	protected UndirectedMotif m;

	protected UndirectedEdge e;

	public UndirectedMotifException(UndirectedMotif m, UndirectedEdge e) {
		this.m = m;
		this.e = e;
	}
}
