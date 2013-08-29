package dna.graph.nodes;

import dna.graph.Element;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.util.MathHelper;

public abstract class Node extends Element implements IElement {
	protected int index;
	protected GraphDataStructure gds;

	public Node(int index, GraphDataStructure gds) {
		this.index = index;
		this.gds = gds;
		this.init();
	}

	public Node(String str, GraphDataStructure gds) {
		this(MathHelper.parseInt(str), gds);
	}

	protected abstract void init();

	public int getIndex() {
		return this.index;
	}

	public abstract boolean hasEdge(Edge e);

	public abstract boolean addEdge(Edge e);

	public abstract boolean removeEdge(Edge e);

	public abstract Iterable<IElement> getEdges();

	public abstract void print();

	public String toString() {
		return "" + this.index;
	}

	public String getStringRepresentation() {
		return Integer.toString(this.index);
	}

	public int hashCode() {
		return Integer.toString(this.index).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Node && ((Node) obj).getIndex() == this.index;
	}

	@Override
	public int compareTo(Element o) {
		if (!(o instanceof Node))
			throw new ClassCastException();
		return this.index - ((Node) o).getIndex();
	}
}
