package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.util.Log;

public class EdgeAddition<E extends Edge> extends EdgeUpdate<E> {

	public EdgeAddition(E edge) {
		super(edge, UpdateType.EdgeAddition);
	}

	public String toString() {
		return "add " + this.edge;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean apply(Graph<? extends Node<E>, ? extends E> graph) {
		Log.debug("=> " + this.toString());
		return ((Graph<Node<E>, E>) graph).addEdge((E) this.edge);
	}

}
