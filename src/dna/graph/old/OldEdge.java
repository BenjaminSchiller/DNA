package dna.graph.old;

import dna.io.etc.Keywords;

/**
 * 
 * Represents a directed edge from a source to a destination node.
 * 
 * @author benni
 * 
 */
public class OldEdge implements Comparable<OldEdge> {
	/**
	 * 
	 * @param src
	 *            source node of the edge
	 * @param dst
	 *            destination node of the edge
	 */
	public OldEdge(OldNode src, OldNode dst) {
		this.src = src;
		this.dst = dst;
	}

	public String toString() {
		return this.src.getIndex() + "->" + this.dst.getIndex();
	}

	/**
	 * 
	 * @return String representation of this edge that can be parsed using the
	 *         static fromString(...) method
	 */
	public String getStringRepresentation() {
		return this.src.getIndex() + Keywords.edgeDelimiter
				+ this.dst.getIndex();
	}

	/**
	 * 
	 * @param s
	 *            String representation of a directed edge
	 * @param g
	 *            graph this directed edge is belonging to (required to obtain node
	 *            object pointers)
	 * @return edge parsed from the given string, nodes are taken from the given
	 *         directed graph object
	 */
	public static OldEdge fromString(String s, OldGraph g) {
		String[] temp = s.split(Keywords.edgeDelimiter);
		int src = Integer.parseInt(temp[0]);
		int dst = Integer.parseInt(temp[1]);
		return new OldEdge(g.getNode(src), g.getNode(dst));
	}

	public boolean equals(Object o) {
		return o != null && o instanceof OldEdge
				&& this.src.getIndex() == ((OldEdge) o).src.getIndex()
				&& this.dst.getIndex() == ((OldEdge) o).dst.getIndex();
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}

	/**
	 * 
	 // * @return new edge with the inverse direction as this one, i.e.,
	 * dst->src
	 */
	public OldEdge invert() {
		return new OldEdge(this.dst, this.src);
	}

	private OldNode src;

	/**
	 * 
	 * @return source node of the edge
	 */
	public OldNode getSrc() {
		return this.src;
	}

	private OldNode dst;

	/**
	 * 
	 * @return destination node of the edge
	 */
	public OldNode getDst() {
		return this.dst;
	}

	@Override
	public int compareTo(OldEdge e) {
		if (e.src != this.src) {
			return this.src.getIndex() - e.src.getIndex();
		}
		return this.dst.getIndex() - e.dst.getIndex();
	}
}
