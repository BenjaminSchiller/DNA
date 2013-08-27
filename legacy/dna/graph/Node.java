package dna.graph;

public abstract class Node<E extends Edge> implements Comparable<Node<E>> {

	protected int index;

	public int getIndex() {
		return this.index;
	}

	public Node(int index) {
		this.index = index;
	}

	public Node(String str) {
		this.index = Integer.parseInt(str);
	}

	public abstract boolean hasEdge(E e);

	public abstract boolean addEdge(E e);

	public abstract boolean removeEdge(E e);

	public abstract Iterable<E> getEdges();

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

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Node
				&& ((Node<E>) obj).getIndex() == this.index;
	}

	@Override
	public int compareTo(Node<E> o) {
		return this.index - o.index;
	}
}
