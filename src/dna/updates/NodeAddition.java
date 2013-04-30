package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.util.Log;

public class NodeAddition<E extends Edge> extends NodeUpdate<E> {

	public NodeAddition(Node<E> node) {
		super(node, UpdateType.NodeAddition);
	}

	public String toString() {
		return "add " + this.node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean apply(Graph<? extends Node<E>, ? extends E> graph) {
		Log.debug("=> " + this.toString());
		return ((Graph<Node<E>, E>) graph).addNode((Node<E>) this.node);
	}

}
