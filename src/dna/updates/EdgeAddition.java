package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.undirected.UndirectedEdge;
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
		if (this.edge instanceof DirectedEdge) {
			boolean success = ((Graph<Node<E>, E>) graph)
					.addEdge((E) this.edge);
			DirectedEdge e = (DirectedEdge) this.edge;
			success &= e.getSrc().addEdge(e);
			success &= e.getDst().addEdge(e);
			return success;
		} else if (this.edge instanceof UndirectedEdge) {
			boolean success = ((Graph<Node<E>, E>) graph)
					.addEdge((E) this.edge);
			UndirectedEdge e = (UndirectedEdge) this.edge;
			success &= e.getNode1().addEdge(e);
			success &= e.getNode2().addEdge(e);
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
