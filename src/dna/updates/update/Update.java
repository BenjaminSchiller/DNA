package dna.updates.update;

import java.util.HashMap;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;

public abstract class Update {

	public static final String TypeDelimiter = "_";

	public static final String WeightDelimiter = ":";

	public static final String EdgeSeparator = "-";

	public static enum UpdateType {
		NA, NR, NW, EA, ER, EW
	};

	// public static enum UpdateType {
	// NODE_ADDITION, NODE_REMOVAL, NODE_WEIGHT, EDGE_ADDITION, EDGE_REMOVAL,
	// EDGE_WEIGHT
	// };

	public boolean apply(IGraph g) {
		return this.apply_(g);
	}

	public abstract boolean apply_(IGraph g);

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

	public static Update fromString(GraphDataStructure gds, IGraph g,
			String str, HashMap<Integer, Node> addedNodes) {
		String[] temp = str.split(Update.TypeDelimiter);
		UpdateType t = UpdateType.valueOf(temp[0]);

		switch (t) {
		case EA:
			return new EdgeAddition(temp[1], gds, g, addedNodes);
		case ER:
			return new EdgeRemoval(temp[1], gds, g);
		case EW:
			return new EdgeWeight(temp[1], gds, g);
		case NA:
			return new NodeAddition(temp[1], gds);
		case NR:
			return new NodeRemoval(temp[1], g);
		case NW:
			return new NodeWeight(temp[1], g);
		default:
			return null;
		}
	}

}
