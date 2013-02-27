package dna.graph;

import dna.settings.Keywords;

/**
 * 
 * Represents a directed edge from a source to a destination node.
 * 
 * @author benni
 * 
 */
public class Edge implements Comparable<Edge> {
	/**
	 * 
	 * @param src
	 *            source node of the edge
	 * @param dst
	 *            destination node of the edge
	 */
	public Edge(Node src, Node dst) {
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
	 *            String representation of an edge
	 * @param g
	 *            graph this edge is belonging to (required to obtain node
	 *            object pointers)
	 * @return edge parsed from the given string, nodes are taken from the given
	 *         graph object
	 */
	public static Edge fromString(String s, Graph g) {
		String[] temp = s.split(Keywords.edgeDelimiter);
		int src = Integer.parseInt(temp[0]);
		int dst = Integer.parseInt(temp[1]);
		return new Edge(g.getNode(src), g.getNode(dst));
	}

	public boolean equals(Object o) {
		return o != null && o instanceof Edge
				&& this.src.getIndex() == ((Edge) o).src.getIndex()
				&& this.dst.getIndex() == ((Edge) o).dst.getIndex();
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}

	/**
	 * 
	 // * @return new edge with the inverse direction as this one, i.e.,
	 * dst->src
	 */
	public Edge invert() {
		return new Edge(this.dst, this.src);
	}

	private Node src;

	/**
	 * 
	 * @return source node of the edge
	 */
	public Node getSrc() {
		return this.src;
	}

	private Node dst;

	/**
	 * 
	 * @return destination node of the edge
	 */
	public Node getDst() {
		return this.dst;
	}

	@Override
	public int compareTo(Edge e) {
		if (e.src != this.src) {
			return this.src.getIndex() - e.src.getIndex();
		}
		return this.dst.getIndex() - e.dst.getIndex();
	}
}
