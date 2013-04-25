package dna.graph;

public abstract class Node<EdgeType extends Edge> implements
		Comparable<Node<EdgeType>> {

	protected int index;

	public int getIndex() {
		return this.index;
	}

	public Node(int index) {
		this.index = index;
	}

	public abstract boolean hasEdge(EdgeType e);

	public abstract boolean addEdge(EdgeType e);

	public abstract boolean removeEdge(EdgeType e);

	public String toString() {
		return "N(" + this.index + ")";
	}

	public String getStringRepresentation() {
		return Integer.toString(this.index);
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}

	@Override
	public int compareTo(Node<EdgeType> o) {
		return this.index - o.index;
	}
}
