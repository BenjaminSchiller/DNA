package dna.graph.edges;

import java.util.Collection;
import java.util.HashSet;

import dna.graph.Edge;

public class EdgesHs<E extends Edge> extends Edges<E> {

	private HashSet<E> edges;

	public EdgesHs(int edges) {
		this.edges = new HashSet<E>(edges);
	}

	@Override
	public E getEdge(E e) {
		for (E edge : this.edges) {
			if (edge.equals(e)) {
				return edge;
			}
		}
		return null;
	}

	@Override
	public int getEdgeCount() {
		return this.edges.size();
	}

	@Override
	public Collection<E> getEdges() {
		return this.edges;
	}

	@Override
	public boolean addEdge(E e) {
		return e != null && this.edges.add(e);
	}

	@Override
	public boolean removeEdge(E e) {
		return e != null && this.edges.remove(e);
	}

	@Override
	public boolean containsEdge(E e) {
		return this.edges.contains(e);
	}

	@Override
	public E getRandomEdge() {
		// TODO implement random directed edge @hs
		return null;
	}

}
