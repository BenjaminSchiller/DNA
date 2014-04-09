package dna.updates.update;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;

public abstract class Update {

	public static final String TypeDelimiter = "#";

	public static final String WeightDelimiter = "@";

	public static final String EdgeSeparator = "--";

	public static enum UpdateType {
		NODE_ADDITION, NODE_REMOVAL, NODE_WEIGHT, EDGE_ADDITION, EDGE_REMOVAL, EDGE_WEIGHT
	};

	public boolean apply(Graph g) {
		return this.apply_(g);
	}

	public abstract boolean apply_(Graph g);

	public abstract UpdateType getType();

	public String asString() {
		return this.getType() + Update.TypeDelimiter + this.asString_();
	}

	protected abstract String asString_();

	public String toString() {
		return this.getType() + ": " + this.asString_();
	}

	protected abstract String toString_();

	@Override
	public int hashCode() {
		return this.hashCode_();
	}

	protected abstract int hashCode_();

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Update)) {
			return false;
		}
		Update u = (Update) o;
		return u.asString().equals(this.asString());
	}

	public static Update fromString(GraphDataStructure gds, Graph g,
			String str, HashMap<Integer, Node> addedNodes) {
		String[] temp = str.split(Update.TypeDelimiter);
		UpdateType t = UpdateType.valueOf(temp[0]);

		switch (t) {
		case EDGE_ADDITION:
			return new EdgeAddition(str, gds, g, addedNodes);
		case EDGE_REMOVAL:
			return new EdgeRemoval(str, gds, g);
		case EDGE_WEIGHT:
			return new EdgeWeight(str, gds, g);
		case NODE_ADDITION:
			return new NodeAddition(str, gds);
		case NODE_REMOVAL:
			return new NodeRemoval(str, g);
		case NODE_WEIGHT:
			return new NodeWeight(str, g);
		default:
			return null;
		}
	}

}
