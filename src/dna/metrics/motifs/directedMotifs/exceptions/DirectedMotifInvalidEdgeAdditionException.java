package dna.metrics.motifs.directedMotifs.exceptions;

import dna.graph.edges.DirectedEdge;
import dna.metrics.motifs.directedMotifs.DirectedMotif;

public class DirectedMotifInvalidEdgeAdditionException extends
		DirectedMotifException {

	private static final long serialVersionUID = -2311223986443025964L;

	public DirectedMotifInvalidEdgeAdditionException(DirectedMotif m,
			DirectedEdge e) {
		super(m, e);
	}

	public String toString() {
		return "cennot add edge " + this.e + " to motif:\n" + this.m.toString();
	}

}
