package dna.graph.nodes;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;

public class UndirectedWeightedNode extends UndirectedNode implements
		IWeightedNode {

	protected Weight weight;

	public UndirectedWeightedNode(int i, GraphDataStructure gds) {
		super(i, gds);
	}

	public UndirectedWeightedNode(int i, Weight weight, GraphDataStructure gds) {
		this(i, gds);
		this.weight = weight;
	}

	public UndirectedWeightedNode(String str, GraphDataStructure gds) {
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
	public String asString() {
		return super.asString() + Weight.WeightDelimiter
				+ this.weight.asString();
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight().asString() + "]";
	}

}
