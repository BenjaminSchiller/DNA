package dna.updates;

import dna.graph.edges.Edge;

public abstract class EdgeUpdate<E extends Edge> extends Update<E> {

	protected E edge;

	public EdgeUpdate(E edge, UpdateType type) {
		super(type);
		this.edge = edge;
	}

	public E getEdge() {
		return this.edge;
	}

}
