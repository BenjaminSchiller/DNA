package dna.updates;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Config;
import dna.util.Log;

public class NodeWeightUpdate<E extends Edge> extends NodeUpdate<E> {

	private double weight;

	public NodeWeightUpdate(Node node, double weight) {
		super(node, UpdateType.NodeWeithUpdate);
		this.weight = weight;
	}

	public double getWeight() {
		return this.weight;
	}

	public String toString() {
		return "w(" + this.node + ") = " + this.weight;
	}

	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		((WeightedNode) this.node).setWeight(this.weight);
		return true;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.node.getStringRepresentation()
				+ Config.get("UPDATE_DELIMITER2") + this.weight;
	}

}
