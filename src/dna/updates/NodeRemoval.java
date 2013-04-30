package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.util.Log;

public class NodeRemoval<E extends Edge> extends NodeUpdate<E> {

	public NodeRemoval(Node<E> node) {
		super(node, UpdateType.NodeRemoval);
	}

	public String toString() {
		return "remove " + this.node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean apply(
			Graph<? extends Node<E>, ? extends E> graph) {
		Log.debug("=> " + this.toString());
		return ((Graph<Node<E>, E>) graph)
				.removeNode((Node<E>) this.node);
	}

}
