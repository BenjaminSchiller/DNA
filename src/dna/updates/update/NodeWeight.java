package dna.updates.update;

import dna.graph.Graph;
import dna.graph.nodes.IWeightedNode;
import dna.graph.weights.IWeighted;
import dna.util.Config;

public class NodeWeight extends NodeUpdate {

	private Object weight;

	public NodeWeight(IWeightedNode node, Object weight) {
		super(UpdateType.NODE_WEIGHT, node);
		this.weight = weight;
	}

	@Override
	public boolean apply_(Graph g) {
		((IWeighted) this.node).setWeight(this.weight);
		return g.getNode(this.node.getIndex()) == this.node;
	}

	@Override
	protected String getStringRepresentation_() {
		return super.getStringRepresentation_()
				+ Config.get("UPDATE_DELIMITER2") + this.weight;
	}

	@Override
	protected String toString_() {
		return super.toString_() + " [" + this.weight + "]";
	}

}
