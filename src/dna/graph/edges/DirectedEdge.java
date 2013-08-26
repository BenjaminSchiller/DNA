package dna.graph.edges;

import dna.graph.Element;
import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.io.etc.Keywords;

public class DirectedEdge extends Edge {
	protected Node src;
	protected Node dst;

	public DirectedEdge(Node src, Node dst) {
		this.src = src;
		this.dst = dst;
	}

	public DirectedEdge(String s, Graph g) {
		String[] temp = s.split(Keywords.directedEdgeDelimiter);
		DirectedNode src = (DirectedNode) g.getNode(Integer.parseInt(temp[0]));
		DirectedNode dst = (DirectedNode) g.getNode(Integer.parseInt(temp[1]));
		this.src = src;
		this.dst = dst;
	}

	public Node getSrc() {
		return this.src;
	}

	public Node getDst() {
		return this.dst;
	}

	public String toString() {
		return this.src.getIndex() + " -> " + this.dst.getIndex();
	}

	@Override
	public String getStringRepresentation() {
		return this.src.getIndex() + Keywords.directedEdgeDelimiter + this.dst.getIndex();
	}

	public boolean equals(Object o) {
		return o != null && o instanceof DirectedEdge && this.src.getIndex() == ((DirectedEdge) o).src.getIndex()
				&& this.dst.getIndex() == ((DirectedEdge) o).dst.getIndex();
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
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

}
