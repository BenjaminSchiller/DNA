package dna.graph.nodes;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weightsNew.IWeightedNode;
import dna.graph.weightsNew.Weight;

public class DirectedWeightedNode extends DirectedNode implements IWeightedNode {

	protected Weight weight;

	public DirectedWeightedNode(int i, GraphDataStructure gds) {
		super(i, gds);
	}

	public DirectedWeightedNode(int i, Weight weight, GraphDataStructure gds) {
		this(i, gds);
		this.weight = weight;
	}

	public DirectedWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Weight.WeightDelimiter)[0], gds);
		this.weight = gds.newNodeWeight(str.split(Weight.WeightDelimiter)[1]);
	}

	@Override
	public Weight getWeight() {
		return this.weight;
	}

	@Override
	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	@Override
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Weight.WeightDelimiter
				+ this.weight.asString();
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight().asString() + "]";
	}

}
