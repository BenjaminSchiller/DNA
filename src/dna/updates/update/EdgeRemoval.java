package dna.updates.update;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;

public class EdgeRemoval extends EdgeUpdate {

	public EdgeRemoval(IEdge edge) {
		super(UpdateType.EDGE_REMOVAL, edge);
	}

	@Override
	public boolean apply_(Graph g) {
		boolean success = g.removeEdge((Edge) this.edge);
		success &= this.edge.disconnectFromNodes();
		return success;
	}

}
