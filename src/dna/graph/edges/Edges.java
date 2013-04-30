package dna.graph.edges;

import java.util.Collection;

import dna.graph.Edge;

public abstract class Edges<E extends Edge> {

	public abstract int getEdgeCount();

	public abstract Collection<E> getEdges();

	public abstract boolean addEdge(E e);

	public abstract boolean removeEdge(E e);

	public abstract boolean containsEdge(E e);

	public abstract E getRandomEdge();

}
