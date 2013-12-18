package dna.metrics.motifs.directedMotifs.exceptions;

import dna.graph.edges.DirectedEdge;
import dna.metrics.motifs.directedMotifs.DirectedMotif;

public abstract class DirectedMotifException extends Exception {

	private static final long serialVersionUID = -1197189476607065524L;

	protected DirectedMotif m;

	protected DirectedEdge e;

	public DirectedMotifException(DirectedMotif m, DirectedEdge e) {
		this.m = m;
		this.e = e;
	}
}
