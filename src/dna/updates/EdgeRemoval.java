package dna.updates;

import dna.graph.Graph;
import dna.graph.edges.Edge;
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
		boolean success = graph.removeEdge(this.edge);
		success &= this.edge.disconnectFromNodes();
		return success;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.edge.getStringRepresentation();
	}

}
