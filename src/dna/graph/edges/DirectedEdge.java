package dna.graph.edges;

import java.util.HashMap;

import dna.graph.Element;
import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.util.MathHelper;

public class DirectedEdge extends Edge {

	public static final String separator = "->";

	public DirectedEdge(DirectedNode src, DirectedNode dst) {
		init(src, dst);
	}

	public DirectedEdge(String s, Graph g) {
		String[] temp = s.split(DirectedEdge.separator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into a directed edge");
		}
		DirectedNode src = (DirectedNode) g.getNode(MathHelper
				.parseInt(temp[0]));
		DirectedNode dst = (DirectedNode) g.getNode(MathHelper
				.parseInt(temp[1]));
		init(src, dst);
	}

	public DirectedEdge(String s, Graph g, HashMap<Integer, Node> addedNodes) {
		String[] temp = s.split(DirectedEdge.separator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into a directed edge");
		}
		int index1 = MathHelper.parseInt(temp[0]);
		int index2 = MathHelper.parseInt(temp[1]);

		DirectedNode src;
		DirectedNode dst;

		if (addedNodes.containsKey(index1)) {
			src = (DirectedNode) addedNodes.get(index1);
		} else {
			src = (DirectedNode) g.getNode(index1);
		}
		if (addedNodes.containsKey(index2)) {
			dst = (DirectedNode) addedNodes.get(index2);
		} else {
			dst = (DirectedNode) g.getNode(index2);
		}
		init(src, dst);
	}

	public DirectedNode getSrc() {
		return (DirectedNode) getN1();
	}

	public DirectedNode getDst() {
		return (DirectedNode) getN2();
	}
	
	public int getSrcIndex() {
		return getN1().getIndex();
	}
	
	public int getDstIndex() {
		return getN2().getIndex();
	}

	public String toString() {
		return getSrcIndex() + " -> " + getDstIndex();
	}

	@Override
	public String asString() {
		return this.getN1().getIndex() + DirectedEdge.separator
				+ this.getN2().getIndex();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof DirectedEdge))
			return false;

		DirectedEdge oCasted = (DirectedEdge) o;
		if (oCasted.getSrc() == null || oCasted.getDst() == null)
			return false;

		return this.getSrcIndex() == oCasted.getSrcIndex()
				&& this.getDstIndex() == oCasted.getDstIndex();
	}

	@Override
	public int compareTo(Element eIn) {
		if (!(eIn instanceof DirectedEdge))
			throw new ClassCastException();

		DirectedEdge e = (DirectedEdge) eIn;

		if (this.getN1() != e.getN1()) {
			return this.getN1().getIndex() - e.getN1().getIndex();
		}
		return this.getN2().getIndex() - e.getN2().getIndex();
	}

	public DirectedEdge invert() {
		return new DirectedEdge((DirectedNode) this.getN2(),
				(DirectedNode) this.getN1());
	}

	public boolean connectToNodes() {
		boolean addSrc = this.getSrc().addEdge(this);
		boolean addDst = this.getDst().addEdge(this);
		return addSrc && addDst;
	}

	public boolean disconnectFromNodes() {
		boolean remSrc = this.getSrc().removeEdge(this);
		boolean remDst = this.getDst().removeEdge(this);
		return remSrc && remDst;
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
