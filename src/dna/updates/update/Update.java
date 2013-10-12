package dna.updates.update;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.IDoubleWeighted;
import dna.graph.weights.IIntWeighted;
import dna.util.Config;
import dna.util.Log;

public abstract class Update {
	public static enum UpdateType {
		NODE_ADDITION, NODE_REMOVAL, NODE_WEIGHT, EDGE_ADDITION, EDGE_REMOVAL, EDGE_WEIGHT
	};

	private UpdateType type;

	public Update(UpdateType type) {
		this.type = type;
	}

	public UpdateType getType() {
		return this.type;
	}

	public boolean apply(Graph g) {
		// System.out.println("=> " + this.toString());
		return this.apply_(g);
	}

	public abstract boolean apply_(Graph g);

	public String getStringRepresentation() {
		return this.type + Config.get("UPDATE_DELIMITER1")
				+ this.getStringRepresentation_();
	}

	protected abstract String getStringRepresentation_();

	public String toString() {
		return this.type + ": " + this.toString_();
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
		return u.getStringRepresentation().equals(
				this.getStringRepresentation());
	}

	public static Update fromString(GraphDataStructure gds, Graph g,
			String str, HashMap<Integer, Node> addedNodes,
			HashSet<Edge> addedEdges) {
		String[] temp = str.split(Config.get("UPDATE_DELIMITER1"));
		UpdateType t = UpdateType.valueOf(temp[0]);
		String[] temp1;
		Node n_, n;
		Edge e_, e;
		Object w;

		switch (t) {
		case NODE_ADDITION:
			n = gds.newNodeInstance(temp[1]);
			return new NodeAddition(n);
		case NODE_REMOVAL:
			n_ = gds.newNodeInstance(temp[1]);
			if (addedNodes.containsKey(n_.getIndex())) {
				n = addedNodes.get(n_.getIndex());
			} else {
				n = g.getNode(n_.getIndex());
			}
			return new NodeRemoval(n);
		case NODE_WEIGHT:
			temp1 = temp[1].split(Config.get("UPDATE_DELIMITER2"));
			n_ = gds.newNodeInstance(temp1[0]);
			if (addedNodes.containsKey(n_.getIndex())) {
				n = addedNodes.get(n_.getIndex());
			} else {
				n = g.getNode(n_.getIndex());
			}
			if (n instanceof IDoubleWeighted) {
				w = Double.parseDouble(temp1[1]);
			} else if (n instanceof IIntWeighted) {
				w = Integer.parseInt(temp1[1]);
			} else {
				Log.error("unknown weight type for node type " + n.getClass());
				w = null;
			}
			return new NodeWeight((IWeightedNode) n, w);
		case EDGE_ADDITION:
			e = gds.newEdgeInstance(temp[1], g, addedNodes);
			return new EdgeAddition(e);
		case EDGE_REMOVAL:
			e_ = gds.newEdgeInstance(temp[1], g, addedNodes);
			e = g.getEdge(e_);
			return new EdgeRemoval(e);
		case EDGE_WEIGHT:
			temp1 = temp[1].split(Config.get("UPDATE_DELIMITER2"));
			e_ = gds.newEdgeInstance(temp1[0], g, addedNodes);
			e = g.getEdge(e_);
			if (e instanceof IDoubleWeighted) {
				w = Double.parseDouble(temp1[1]);
			} else if (e instanceof IIntWeighted) {
				w = Integer.parseInt(temp1[1]);
			} else {
				Log.error("unknown weight type for edge type " + e.getClass());
				w = null;
			}
			return new EdgeWeight((IWeightedEdge) e, w);
		default:
			Log.error("unknown update type " + t);
			return null;

		}
	}
}
