package dna.updates.update;

import dna.graph.Graph;
import dna.graph.weightsNew.IWeightedNode;
import dna.graph.weightsNew.Weight;

public class NodeWeight extends NodeUpdate {

	protected Weight weight;

	public Weight getWeight() {
		return this.weight;
	}

	public NodeWeight(IWeightedNode node, Weight weight) {
		super(node);
		this.weight = weight;
	}

	public NodeWeight(String str, Graph g) {
		super(null);
		String[] temp = str.split(Update.WeightDelimiter);
		this.node = g.getNode(Integer.parseInt(temp[0]));
		this.weight = Weight.fromString(temp[1]);
	}

	@Override
	public boolean apply_(Graph g) {
		((IWeightedNode) this.node).setWeight(this.weight);
		return true;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.NODE_WEIGHT;
	}

	@Override
	protected String asString_() {
		return this.node.getIndex() + Update.WeightDelimiter
				+ this.weight.asString();
	}

	@Override
	protected String toString_() {
		return this.node.getIndex() + " @ " + this.weight.toString();
	}

}
