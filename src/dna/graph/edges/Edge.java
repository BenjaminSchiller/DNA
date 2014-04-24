package dna.graph.edges;

import dna.graph.Element;
import dna.graph.nodes.Node;

public abstract class Edge extends Element implements IEdge {
	private Node n1;
	private Node n2;

	protected void init(Node n1, Node n2) {
		this.n1 = n1;
		this.n2 = n2;
	}
	
	public Node getN1() {
		return n1;
	}
	
	public Node getN2() {
		return n2;
	}
	
	public int getN1Index() {
		return n1.getIndex();
	}
	
	public int getN2Index() {
		return n2.getIndex();
	}
	
	public String getHashString() {
		return Integer.toString(this.hashCode());
	}
	
	public int hashCode() {
		return getHashcode(n1.getIndex(), n2.getIndex());
	}
	
	/**
	 * 
	 * @param n
	 *            node to get the differing one connected by this edge
	 * @return null of the given node is not one of the two nodes connected by
	 *         this edge; otherwise, the connected node different from this one
	 *         is returned
	 */
	public Node getDifferingNode(Node n) {
		if (this.getN1().equals(n)) {
			return this.getN2();
		} else if (this.getN2().equals(n)) {
			return this.getN1();
		} else {
			return null;
		}
	}
	
	public static int getHashcode(Node n1, Node n2) {
		return getHashcode(n1.getIndex(), n2.getIndex());
	}
	
	public static int getHashcode(int n1Index, int n2Index) {
		return n1Index * (int) Math.pow(2, 15) + n2Index;
	}
}
