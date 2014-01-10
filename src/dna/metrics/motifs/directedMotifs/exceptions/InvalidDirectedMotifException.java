package dna.metrics.motifs.directedMotifs.exceptions;

import dna.graph.nodes.DirectedNode;

public class InvalidDirectedMotifException extends Exception {

	private static final long serialVersionUID = 301944351486577271L;

	private DirectedNode a;
	private DirectedNode b;
	private DirectedNode c;

	public InvalidDirectedMotifException(DirectedNode a, DirectedNode b,
			DirectedNode c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public String toString() {
		return this.a.getIndex() + ", " + this.b.getIndex() + ", "
				+ this.c.getIndex() + " do not form a valid motif";
	}

}
