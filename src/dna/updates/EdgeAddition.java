package dna.updates;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.util.Log;

public class EdgeAddition<E extends Edge> extends EdgeUpdate<E> {

	public EdgeAddition(E edge) {
		super(edge, UpdateType.EdgeAddition);
	}

	public String toString() {
		return "add " + this.edge;
	}

	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		boolean success = graph.addEdge((E) this.edge);
		success &= this.edge.connectToNodes();
		return success;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.edge.getStringRepresentation();
	}

}
