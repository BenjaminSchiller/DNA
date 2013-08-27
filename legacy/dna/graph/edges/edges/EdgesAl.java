package dna.graph.edges;

import java.util.ArrayList;
import java.util.Collection;

import dna.graph.Edge;
import dna.util.Rand;

public class EdgesAl<E extends Edge> extends Edges<E> {

	private ArrayList<E> edges;

	public EdgesAl(int edges) {
		this.edges = new ArrayList<E>(edges);
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
		return !this.edges.contains(e) && this.edges.add(e);
	}

	@Override
	public boolean removeEdge(E e) {
		return this.edges.remove(e);
	}

	@Override
	public boolean containsEdge(E e) {
		return this.edges.contains(e);
	}

	@Override
	public E getRandomEdge() {
		return this.edges.get(Rand.rand.nextInt(this.edges.size()));
	}

}
