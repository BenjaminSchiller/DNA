package dna.depr.metrics.motifs.directedMotifs.exceptions;

import dna.depr.metrics.motifs.directedMotifs.DirectedMotif;
import dna.graph.edges.DirectedEdge;

@Deprecated
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
