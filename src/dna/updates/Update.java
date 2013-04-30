package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;

public abstract class Update<E extends Edge> {

	public static enum UpdateType {
		NodeAddition, NodeRemoval, NodeWeithUpdate, EdgeAddition, EdgeRemoval, EdgeWeightUpdate
	};

	private UpdateType type;

	public Update(UpdateType type) {
		this.type = type;
	}

	public UpdateType getType() {
		return this.type;
	}

	public abstract boolean apply(
			Graph<? extends Node<E>, ? extends E> graph);

	// TODO read update from string
}
