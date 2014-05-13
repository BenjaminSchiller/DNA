package dna.updates.update;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.Node;
import dna.util.Log;

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
		if (!g.addEdge((Edge) this.edge)) {
			Log.error("could not add edge " + this.edge);
			if (g.containsEdge((Edge) this.edge)) {
				Edge old = g.getEdge(this.edge.getN1(), this.edge.getN2());
				Log.error("edge already exists! " + old + " @ hashes "
						+ old.hashCode() + " vs. " + this.edge.hashCode()
						+ " (" + old.equals(this.edge));
			} else {
				Log.error("BUT edge not contained in list...");
			}
			return false;
		}
		if (!this.edge.connectToNodes()) {
			return false;
		}
		return true;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.EA;
	}

	@Override
	protected String asString_() {
		return this.edge.asString();
	}

	@Override
	protected String toString_() {
		return this.edge.toString();
	}

}
