package dna.updates;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Log;

public class NodeAddition<E extends Edge> extends NodeUpdate<E> {

	public NodeAddition(Node node) {
		super(node, UpdateType.NodeAddition);
	}

	public String toString() {
		return "add " + this.node;
	}

	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		return graph.addNode(this.node);
	}

	@Override
	protected String getStringRepresentation_() {
		return this.node.getStringRepresentation();
	}

}
