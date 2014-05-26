package dna.graph.nodes;

import dna.graph.Element;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.util.MathHelper;

public abstract class Node extends Element implements INode {
	protected int index;

	public Node(int index, GraphDataStructure gds) {
		this.index = index;
		this.init(gds);
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

	public String asString() {
		return Integer.toString(this.index);
	}
	
	public boolean hasEdge(Node n1, Node n2) {
		return hasEdge(GraphDataStructure.getCurrent().getDummyEdge(n1, n2));
	}

	public boolean hasEdge(int n1, int n2) {
		return hasEdge(GraphDataStructure.getCurrent().getDummyEdge(n1, n2));
	}

	public int hashCode() {
		return this.index;
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
	
	public abstract void switchDataStructure(ListType type, IDataStructure newDatastructure);
}
