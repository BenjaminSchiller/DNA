package dna.graph.directed;

import dna.graph.Edge;
import dna.io.etc.Keywords;

public class DirectedEdge extends Edge implements Comparable<DirectedEdge> {
	protected DirectedNode src;

	public DirectedNode getSrc() {
		return this.src;
	}

	protected DirectedNode dst;

	public DirectedNode getDst() {
		return this.dst;
	}

	public DirectedEdge(String s, DirectedGraph g) {
		String[] temp = s.split(Keywords.directedEdgeDelimiter);
		DirectedNode src = g.getNode(Integer.parseInt(temp[0]));
		DirectedNode dst = g.getNode(Integer.parseInt(temp[1]));
		this.src = src;
		this.dst = dst;
	}

	public DirectedEdge(DirectedNode src, DirectedNode dst) {
		this.src = src;
		this.dst = dst;
	}

	public String toString() {
		return this.src.getIndex() + " -> " + this.dst.getIndex();
	}

	/**
	 * 
	 * @return String representation of this edge
	 */
	public String getStringRepresentation() {
		return this.src.getIndex() + Keywords.directedEdgeDelimiter
				+ this.dst.getIndex();
	}

	public boolean equals(Object o) {
		return o != null && o instanceof DirectedEdge
				&& this.src.getIndex() == ((DirectedEdge) o).src.getIndex()
				&& this.dst.getIndex() == ((DirectedEdge) o).dst.getIndex();
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}

	@Override
	public int compareTo(DirectedEdge e) {
		if (this.src != e.src) {
			return this.src.getIndex() - e.src.getIndex();
		}
		return this.dst.getIndex() - e.dst.getIndex();
	}

}
