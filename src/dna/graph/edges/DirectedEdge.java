package dna.graph.edges;

import dna.graph.Element;
import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.util.Config;
import dna.util.MathHelper;

public class DirectedEdge extends Edge {
	protected DirectedNode src;
	protected DirectedNode dst;

	public DirectedEdge(DirectedNode src, DirectedNode dst) {
		this.src = src;
		this.dst = dst;
	}

	public DirectedEdge(String s, Graph g) {
		String[] temp = s.split(Config.get("EDGE_DIRECTED_DELIMITER"));
		DirectedNode src = (DirectedNode) g.getNode(MathHelper
				.parseInt(temp[0]));
		DirectedNode dst = (DirectedNode) g.getNode(MathHelper
				.parseInt(temp[1]));
		this.src = src;
		this.dst = dst;
	}

	public DirectedNode getSrc() {
		return this.src;
	}

	public DirectedNode getDst() {
		return this.dst;
	}

	public String toString() {
		return this.src.getIndex() + " -> " + this.dst.getIndex();
	}

	@Override
	public String getStringRepresentation() {
		return this.src.getIndex() + Config.get("EDGE_DIRECTED_DELIMITER")
				+ this.dst.getIndex();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof DirectedEdge))
			return false;

		DirectedEdge oCasted = (DirectedEdge) o;
		if (oCasted.getSrc() == null || oCasted.getDst() == null)
			return false;

		return this.getSrc().getIndex() == oCasted.getSrc().getIndex()
				&& this.getDst().getIndex() == oCasted.getDst().getIndex();
	}

	public int hashCode() {
		String s = "" + this.getSrc().getIndex() + this.getDst().getIndex();
		return s.hashCode();
	}

	@Override
	public int compareTo(Element eIn) {
		if (!(eIn instanceof DirectedEdge))
			throw new ClassCastException();

		DirectedEdge e = (DirectedEdge) eIn;

		if (this.src != e.src) {
			return this.src.getIndex() - e.src.getIndex();
		}
		return this.dst.getIndex() - e.dst.getIndex();
	}

	public DirectedEdge invert() {
		return new DirectedEdge(this.dst, this.src);
	}

	public Node getDifferingNode(Node n) {
		if (n instanceof DirectedNode)
			return this.getDifferingNode((DirectedNode) n);
		return null;
	}

	public DirectedNode getDifferingNode(DirectedNode n) {
		if (this.src.equals(n))
			return this.dst;
		else if (this.dst.equals(n))
			return this.src;
		else
			return null;
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

}
