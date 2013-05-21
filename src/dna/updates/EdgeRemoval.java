package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.undirected.UndirectedEdge;
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
		if (this.edge instanceof DirectedEdge) {
			boolean success = ((Graph<Node<E>, E>) graph)
					.removeEdge((E) this.edge);
			DirectedEdge e = (DirectedEdge) this.edge;
			success &= e.getSrc().removeEdge(e);
			success &= e.getDst().removeEdge(e);
			return success;
		} else if (this.edge instanceof UndirectedEdge) {
			boolean success = ((Graph<Node<E>, E>) graph)
					.removeEdge((E) this.edge);
			UndirectedEdge e = (UndirectedEdge) this.edge;
			success &= e.getNode1().removeEdge(e);
			success &= e.getNode2().removeEdge(e);
			return success;
		}
		Log.error("EdgeAddition - unkown edge type " + this.edge.getClass());
		return false;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.edge.getStringRepresentation();
	}

}
