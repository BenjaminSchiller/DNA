package dna.depr.metrics.motifs.undirectedMotifs.exceptions;

import dna.depr.metrics.motifs.undirectedMotifs.UndirectedMotif;
import dna.graph.edges.UndirectedEdge;

@Deprecated
public class UndirectedMotifDeletingOnlyEdgeException extends UndirectedMotifException {

	private static final long serialVersionUID = 609272481545179844L;

	public UndirectedMotifDeletingOnlyEdgeException(UndirectedMotif m, UndirectedEdge e) {
		super(m, e);
	}

	public String toString() {
		return "the removed edge " + this.e
				+ " was the only edge of the PRE1 motif:\n" + m.toString();
	}

}
