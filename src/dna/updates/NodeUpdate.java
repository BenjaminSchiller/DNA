package dna.updates;

import dna.graph.Edge;
import dna.graph.Node;

public abstract class NodeUpdate<E extends Edge> extends
		Update<E> {

	protected Node<E> node;

	public NodeUpdate(Node<E> node, UpdateType type) {
		super(type);
		this.node = node;
	}

	public Node<E> getNode() {
		return this.node;
	}

}
