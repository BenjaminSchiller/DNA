package dna.updates;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.util.Config;

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

	public abstract boolean apply(Graph graph);

	public String getStringRepresentation() {
		return this.type + Config.get("UPDATE_DELIMITER1")
				+ this.getStringRepresentation_();
	}

	protected abstract String getStringRepresentation_();
}
