package dna.graph.undirected;

import dna.graph.Edge;
import dna.io.etc.Keywords;

/**
 * Represents an undirected edge from one node to another.
 * 
 * @author benni
 * 
 */
public class UndirectedEdge extends Edge implements Comparable<UndirectedEdge> {

	/**
	 * 
	 * The node with the lower index is stored as the first node. In case
	 * node1.index > node2.index, they are stored in changed order.
	 * 
	 * @param node1
	 *            first node connected by this edge
	 * @param node2
	 *            second node connected by this edge
	 */
	public UndirectedEdge(UndirectedNode node1, UndirectedNode node2) {
		this.init(node1, node2);
	}

	/**
	 * 
	 * @param s
	 *            String representation of an undirected edge
	 * @param g
	 *            graph this undirected edge is belonging to (required to obtain
	 *            node object pointers)
	 */
	public UndirectedEdge(String s, UndirectedGraph g) {
		String[] temp = s.split(Keywords.undirectedEdgeDelimiter);
		UndirectedNode node1 = g.getNode(Integer.parseInt(temp[0]));
		UndirectedNode node2 = g.getNode(Integer.parseInt(temp[1]));
		this.init(node1, node2);
	}

	/**
	 * 
	 * @return String representation of this edge
	 */
	public String getStringRepresentation() {
		return this.node1.getIndex() + Keywords.undirectedEdgeDelimiter
				+ this.node2.getIndex();
	}
	
	protected UndirectedNode node1;

	/**
	 * 
	 * @return first node connected by this edge (the node with the lower index)
	 */
	public UndirectedNode getNode1() {
		return this.node1;
	}

	protected UndirectedNode node2;

	/**
	 * 
	 * @return second node connected by this edge (the node with the higher
	 *         index)
	 */
	public UndirectedNode getNode2() {
		return this.node2;
	}

	private void init(UndirectedNode node1, UndirectedNode node2) {
		if (node1.getIndex() > node2.getIndex()) {
			this.node1 = node2;
			this.node2 = node1;
		} else {
			this.node1 = node1;
			this.node2 = node2;
		}
	}

	public String toString() {
		return this.node1.getIndex() + " <-> " + this.node2.getIndex();
	}

	public boolean equals(Object o) {
		return o != null
				&& o instanceof UndirectedEdge
				&& this.node1.getIndex() == ((UndirectedEdge) o).node1
						.getIndex()
				&& this.node2.getIndex() == ((UndirectedEdge) o).node2
						.getIndex();
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}

	@Override
	public int compareTo(UndirectedEdge e) {
		if (this.node1 != e.node1) {
			return this.node1.getIndex() - e.node1.getIndex();
		}
		return this.node2.getIndex() - e.node2.getIndex();
	}

}
