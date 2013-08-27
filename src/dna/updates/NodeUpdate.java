package dna.updates;

import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public abstract class NodeUpdate<E extends Edge> extends Update<E> {

	protected Node node;

	public NodeUpdate(Node node, UpdateType type) {
		super(type);
		this.node = node;
	}

	public Node getNode() {
		return this.node;
	}

}
