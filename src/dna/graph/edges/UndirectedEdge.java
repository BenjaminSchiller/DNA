package dna.graph.edges;

import java.util.HashMap;

import dna.graph.Element;
import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.util.MathHelper;

public class UndirectedEdge extends Edge {

	public static final String separator = "<->";

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
	public UndirectedEdge(String s, Graph g) {
		String[] temp = s.split(UndirectedEdge.separator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into an undirected edge");
		}
		UndirectedNode node1 = (UndirectedNode) g.getNode(MathHelper
				.parseInt(temp[0]));
		UndirectedNode node2 = (UndirectedNode) g.getNode(MathHelper
				.parseInt(temp[1]));
		this.init(node1, node2);
	}

	public UndirectedEdge(String s, Graph g, HashMap<Integer, Node> addedNodes) {
		String[] temp = s.split(UndirectedEdge.separator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into an undirected edge");
		}
		int index1 = MathHelper.parseInt(temp[0]);
		int index2 = MathHelper.parseInt(temp[1]);
		UndirectedNode node1 = null;
		UndirectedNode node2 = null;
		if (addedNodes.containsKey(index1)) {
			node1 = (UndirectedNode) addedNodes.get(index1);
		} else {
			node1 = (UndirectedNode) g.getNode(index1);
		}
		if (addedNodes.containsKey(index2)) {
			node2 = (UndirectedNode) addedNodes.get(index2);
		} else {
			node2 = (UndirectedNode) g.getNode(index2);
		}
		this.init(node1, node2);
	}

	/**
	 * 
	 * @return String representation of this edge
	 */
	public String asString() {
		return this.getN1().getIndex() + UndirectedEdge.separator
				+ this.getN2().getIndex();
	}

	/**
	 * 
	 * @return first node connected by this edge (the node with the lower index)
	 */
	public UndirectedNode getNode1() {
		return (UndirectedNode) this.getN1();
	}
	
	public int getNode1Index() {
		return getNode1().getIndex();
	}

	/**
	 * 
	 * @return second node connected by this edge (the node with the higher
	 *         index)
	 */
	public UndirectedNode getNode2() {
		return (UndirectedNode) this.getN2();
	}
	
	public int getNode2Index() {
		return getNode2().getIndex();
	}

	private void init(UndirectedNode node1, UndirectedNode node2) {
		if (node1.getIndex() > node2.getIndex()) {
			super.init(node2, node1);
		} else {
			super.init(node1, node2);
		}
	}

	public String toString() {
		return this.getN1().getIndex() + " <-> " + this.getN2().getIndex();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof UndirectedEdge))
			return false;

		UndirectedEdge oCasted = (UndirectedEdge) o;
		if (oCasted.getNode1() == null || oCasted.getNode2() == null)
			return false;

		return this.getNode1Index() == oCasted.getNode1Index()
				&& this.getNode2Index() == oCasted.getNode2Index();
	}

	@Override
	public int compareTo(Element eIn) {
		if (!(eIn instanceof UndirectedEdge))
			throw new ClassCastException();

		UndirectedEdge e = (UndirectedEdge) eIn;
		if (this.getN1() != e.getN1()) {
			return this.getN1().getIndex() - e.getN1().getIndex();
		}
		return this.getN2().getIndex() - e.getN2().getIndex();
	}

	public boolean connectToNodes() {
		boolean add1 = this.getNode1().addEdge(this);
		boolean add2 = this.getNode2().addEdge(this);
		return add1 && add2;
	}

	public boolean disconnectFromNodes() {
		boolean rem1 = this.getNode1().removeEdge(this);
		boolean rem2 = this.getNode2().removeEdge(this);
		return rem1 && rem2;
	}

	@Override
	public boolean isConnectedTo(Node n1, Node n2) {
		return (this.getN1().equals(n1) && this.getN2().equals(n2))
				|| (this.getN1().equals(n2) && this.getN2().equals(n1));
	}

	@Override
	public boolean isConnectedTo(Node n1) {
		return this.getN1().equals(n1) || this.getN2().equals(n1);
	}

}
