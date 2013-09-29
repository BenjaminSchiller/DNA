package dna.updates.update;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;

public class EdgeAddition extends EdgeUpdate {

	public EdgeAddition(IEdge edge) {
		super(UpdateType.EDGE_ADDITION, edge);
	}

	@Override
	public boolean apply_(Graph g) {
		boolean success = g.addEdge((Edge) this.edge);
		success &= this.edge.connectToNodes();
		return success;
	}

}
