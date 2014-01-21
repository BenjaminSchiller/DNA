package dna.depr.metrics.motifs.directedMotifs.exceptions;

import dna.depr.metrics.motifs.directedMotifs.DirectedMotif;
import dna.graph.edges.DirectedEdge;

@Deprecated
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
