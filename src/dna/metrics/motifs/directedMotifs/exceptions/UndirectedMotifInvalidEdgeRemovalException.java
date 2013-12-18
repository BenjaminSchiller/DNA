package dna.metrics.motifs.directedMotifs.exceptions;

import dna.graph.edges.DirectedEdge;
import dna.metrics.motifs.directedMotifs.DirectedMotif;

public class UndirectedMotifInvalidEdgeRemovalException extends
		DirectedMotifException {

	private static final long serialVersionUID = -2831057036853373435L;

	public UndirectedMotifInvalidEdgeRemovalException(DirectedMotif m,
			DirectedEdge e) {
		super(m, e);
	}

	public String toString() {
		return "cennot remove edge " + this.e + " from motif:\n"
				+ this.m.toString();
	}

}
