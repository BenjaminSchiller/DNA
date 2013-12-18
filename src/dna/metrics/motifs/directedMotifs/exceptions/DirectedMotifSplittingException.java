package dna.metrics.motifs.directedMotifs.exceptions;

import dna.graph.edges.DirectedEdge;
import dna.metrics.motifs.directedMotifs.DirectedMotif;

public class DirectedMotifSplittingException extends DirectedMotifException {

	private static final long serialVersionUID = -1291945736888777273L;

	public DirectedMotifSplittingException(DirectedMotif m, DirectedEdge e) {
		super(m, e);
	}

	public String toString() {
		return "removing the edge " + this.e + " splits the motif:\n"
				+ this.m.toString();
	}

}
