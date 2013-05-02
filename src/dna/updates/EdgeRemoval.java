package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.util.Log;

public class EdgeRemoval<E extends Edge> extends EdgeUpdate<E> {

	public EdgeRemoval(E edge) {
		super(edge, UpdateType.EdgeRemoval);
	}

	public String toString() {
		return "remove " + this.edge;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean apply(Graph<? extends Node<E>, ? extends E> graph) {
		Log.debug("=> " + this.toString());
		return ((Graph<Node<E>, E>) graph).removeEdge((E) this.edge);
	}

	@Override
	protected String getStringRepresentation_() {
		return this.edge.getStringRepresentation();
	}

}
