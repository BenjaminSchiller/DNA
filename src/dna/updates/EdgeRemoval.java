package dna.updates;

import dna.graph.Graph;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.util.Log;

public class EdgeRemoval<E extends Edge> extends EdgeUpdate<E> {

	public EdgeRemoval(E edge) {
		super(edge, UpdateType.EdgeRemoval);
	}

	public String toString() {
		return "remove " + this.edge;
	}

	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		if (this.edge instanceof DirectedEdge) {
			boolean success = graph.removeEdge(this.edge);
			DirectedEdge e = (DirectedEdge) this.edge;
			success &= e.getSrc().removeEdge(e);
			success &= e.getDst().removeEdge(e);
			return success;
		} else if (this.edge instanceof UndirectedEdge) {
			boolean success = graph.removeEdge(this.edge);
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
