package dna.graph.nodes;

import dna.graph.Element;
import dna.graph.datastructures.GraphDataStructure;
import dna.util.MathHelper;

public abstract class Node extends Element implements INode {
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

	public int getIndex() {
		return this.index;
	}

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
		return obj != null && obj instanceof Node
				&& ((Node) obj).getIndex() == this.index;
	}

	@Override
	public int compareTo(Element o) {
		if (!(o instanceof Node))
			throw new ClassCastException();
		return this.index - ((Node) o).getIndex();
	}
}
