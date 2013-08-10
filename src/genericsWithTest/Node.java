package genericsWithTest;

import genericsWithTest.DataStructures.IEdgeListDatastructure;
import genericsWithTest.DataStructures.INodeListDatastructure;

public abstract class Node extends Element implements IElement {
	protected int index;
	protected Class<? extends IEdgeListDatastructure> edgeListType;
	protected Class<? extends INodeListDatastructure> nodeListType;
	
	public Node(int index, Class<? extends IEdgeListDatastructure> edgeListType, Class<? extends INodeListDatastructure> nodeListType) {
		this.index = index;
		this.edgeListType = edgeListType;
		this.nodeListType = nodeListType;
	}
	
	public Node(String str, Class<? extends IEdgeListDatastructure> edgeListType, Class<? extends INodeListDatastructure> nodeListType) {
		this(Integer.parseInt(str), edgeListType, nodeListType);
	}
	
	protected abstract void init();
	
	public int getIndex() {
		return this.index;
	}

	public abstract boolean hasEdge(Edge e);

	public abstract boolean addEdge(Edge e);

	public abstract boolean removeEdge(Edge e);

	public abstract Iterable<Edge> getEdges();

	public abstract void print();

	public String toString() {
		return "" + this.index;
	}

	public String getStringRepresentation() {
		return Integer.toString(this.index);
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Node
				&& ((Node) obj).getIndex() == this.index;
	}

	@Override
	public int compareTo(Object o) {
		if ( o instanceof Node ) {
			return this.index - ((Node)o).getIndex();
		} else {
			return 0;
		}
	}
}
