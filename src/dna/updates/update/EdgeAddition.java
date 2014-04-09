package dna.updates.update;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.Node;

public class EdgeAddition extends EdgeUpdate {

	public EdgeAddition(IEdge edge) {
		super(edge);
	}

	public EdgeAddition(String str, GraphDataStructure gds, Graph g,
			HashMap<Integer, Node> addedNodes) {
		super(gds.newEdgeInstance(str, g, addedNodes));
	}

	@Override
	public boolean apply_(Graph g) {
		boolean success = g.addEdge((Edge) this.edge);
		success &= this.edge.connectToNodes();
		return success;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.EDGE_ADDITION;
	}

	@Override
	protected String asString_() {
		return this.edge.getStringRepresentation();
	}

	@Override
	protected String toString_() {
		return this.edge.toString();
	}

}
