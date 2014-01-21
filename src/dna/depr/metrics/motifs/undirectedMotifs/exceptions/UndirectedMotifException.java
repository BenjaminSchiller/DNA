package dna.depr.metrics.motifs.undirectedMotifs.exceptions;

import dna.depr.metrics.motifs.undirectedMotifs.UndirectedMotif;
import dna.graph.edges.UndirectedEdge;

@Deprecated
public abstract class UndirectedMotifException extends Exception {
	private static final long serialVersionUID = 8765421972727924545L;

	protected UndirectedMotif m;

	protected UndirectedEdge e;

	public UndirectedMotifException(UndirectedMotif m, UndirectedEdge e) {
		this.m = m;
		this.e = e;
	}
}
