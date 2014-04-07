package dna.graph.nodes;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weightsNew.IWeighted;
import dna.graph.weightsNew.Weight;
import dna.util.Config;

public class DirectedWeightedNode extends DirectedNode implements IWeighted {

	protected Weight weight;

	@Override
	public Weight getWeight() {
		return this.weight;
	}

	@Override
	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	public DirectedWeightedNode(int i, Weight weight, GraphDataStructure gds) {
		super(i, gds);
		this.weight = weight;
	}

	public DirectedWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Weight.WeightDelimiter)[0], gds);
		this.weight = Weight.fromString(str.split(Weight.WeightDelimiter)[1]);
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
