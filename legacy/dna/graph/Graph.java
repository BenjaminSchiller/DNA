package dna.graph;

import java.util.Collection;

public abstract class Graph<N extends Node<E>, E extends Edge> {

	public Graph(String name, long timestamp,
			GraphDatastructures<Graph<N, E>, N, E> ds) {
		this.name = name;
		this.timestamp = timestamp;
		this.ds = ds;
	}

	protected String name;

	public String getName() {
		return this.name;
	}

	protected long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	protected GraphDatastructures<Graph<N, E>, N, E> ds;

	public GraphDatastructures<Graph<N, E>, N, E> getGraphDatastructures() {
		return this.ds;
	}

	public String toString() {
		return this.getName() + " @ " + this.getTimestamp() + " ("
				+ this.getNodeCount() + "/" + this.getEdgeCount() + ")";
	}

	public void print() {
		System.out.println(this.toString());
		System.out.println("  V = " + this.getNodes());
		System.out.println("  E = " + this.getEdges());
	}

	public abstract N getNode(int index);

	public abstract int getMaxNodeIndex();

	public abstract int getNodeCount();

	public abstract Collection<N> getNodes();

	public abstract boolean addNode(N n);

	public abstract boolean removeNode(N n);

	public abstract boolean containsNode(N n);

	public abstract Node<E> getRandomNode();

	public abstract E getEdge(E e);

	public abstract int getEdgeCount();

	public abstract Collection<E> getEdges();

	public abstract boolean addEdge(E e);

	public abstract boolean removeEdge(E e);

	public abstract boolean containsEdge(E e);

	public abstract E getRandomEdge();
}
