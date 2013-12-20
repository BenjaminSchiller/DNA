package dna.metrics.motifs.directedMotifs.exceptions;

import dna.graph.edges.DirectedEdge;
import dna.metrics.motifs.directedMotifs.DirectedMotif;

public class DirectedMotifInvalidEdgeRemovalException extends
		DirectedMotifException {

	private static final long serialVersionUID = -9012895138024499008L;

	public DirectedMotifInvalidEdgeRemovalException(DirectedMotif m,
			DirectedEdge e) {
		super(m, e);
	}

	public String toString() {
		return "cennot remove edge " + this.e + " from motif:\n"
				+ this.m.toString();
	}

}
