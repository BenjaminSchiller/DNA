package dna.metrics.motifs.exceptions;

import dna.graph.edges.UndirectedEdge;
import dna.metrics.motifs.UndirectedMotif;

public class UndirectedMotifSplittingException extends UndirectedMotifException {

	private static final long serialVersionUID = 4054514685260218064L;

	public UndirectedMotifSplittingException(UndirectedMotif m, UndirectedEdge e) {
		super(m, e);
	}

	public String toString() {
		return "removing the edge " + this.e
				+ " splits the UM1 motif into two motifs:\n" + this.m.toString()
				+ "\n===>\n" + this.getM1() + "\n&\n" + this.getM2();
	}

	public UndirectedMotif getM1() {
		return new UndirectedMotif(this.m.getA(), this.m.getC());
	}

	public UndirectedMotif getM2() {
		return new UndirectedMotif(this.m.getB(), this.m.getD());
	}

}
