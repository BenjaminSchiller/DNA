package Graph.Edges;

import Graph.Element;
import Graph.ReadableGraph;
import Graph.Nodes.Node;
import Graph.Nodes.UndirectedNode;
import Utils.Keywords;

public class UndirectedEdge extends Edge {

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
	public UndirectedEdge(Node node1, Node node2) {
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
	public UndirectedEdge(String s, ReadableGraph g) {
		String[] temp = s.split(Keywords.undirectedEdgeDelimiter);
		UndirectedNode node1 = (UndirectedNode) g.getNode(Integer.parseInt(temp[0]));
		UndirectedNode node2 = (UndirectedNode) g.getNode(Integer.parseInt(temp[1]));
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

	protected Node node1;

	/**
	 * 
	 * @return first node connected by this edge (the node with the lower index)
	 */
	public Node getNode1() {
		return this.node1;
	}

	protected Node node2;

	/**
	 * 
	 * @return second node connected by this edge (the node with the higher
	 *         index)
	 */
	public Node getNode2() {
		return this.node2;
	}

	private void init(Node node1, Node node2) {
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

	/**
	 * 
	 * @param n
	 *            node to get the differing one connected by this edge
	 * @return null of the given node is not one of the two nodes connected by
	 *         this edge; otherwise, the connected node different from this one
	 *         is returned
	 */
	public Node getDifferingNode(UndirectedNode n) {
		if (this.node1.equals(n)) {
			return this.node2;
		} else if (this.node2.equals(n)) {
			return this.node1;
		} else {
			return null;
		}
	}	
	
	@Override
	public int compareTo(Element eIn) {
		if ( !(eIn instanceof UndirectedEdge)) throw new ClassCastException();
		
		UndirectedEdge e = (UndirectedEdge)eIn;
		if (this.node1 != e.node1) {
			return this.node1.getIndex() - e.node1.getIndex();
		}
		return this.node2.getIndex() - e.node2.getIndex();
	}

}
