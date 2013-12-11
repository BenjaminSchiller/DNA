package dna.metrics.motifs.exceptions;

import dna.graph.edges.UndirectedEdge;
import dna.metrics.motifs.UndirectedMotif;

public class UndirectedMotifInvalidEdgeRemovalException extends
		UndirectedMotifException {

	private static final long serialVersionUID = -4032835654069367304L;

	public UndirectedMotifInvalidEdgeRemovalException(UndirectedMotif m,
			UndirectedEdge e) {
		super(m, e);
	}

	public String toString() {
		return "cennot remove edge " + this.e + " from motif:\n"
				+ this.m.toString();
	}

}
