package dna.graph.nodes;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IIntWeighted;
import dna.io.etc.Keywords;

public class UndirectedIntWeightedNode extends UndirectedNode implements
		IWeightedNode<Integer>, IIntWeighted {
	private int weight;

	public UndirectedIntWeightedNode(int i, Integer weight,
			GraphDataStructure gds) {
		super(i, gds);
		this.setWeight(weight);
	}

	public UndirectedIntWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Keywords.nodeWeightDelimiter)[0], gds);
		if (str.contains(Keywords.nodeWeightDelimiter)) {
			this.weight = Integer.parseInt(str
					.split(Keywords.nodeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}

	public UndirectedIntWeightedNode(int i, GraphDataStructure gds) {
		this(i, 0, gds);
	}

	@Override
	public void setWeight(Integer newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Integer getWeight() {
		return this.weight;
	}

	@Override
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.nodeWeightDelimiter
				+ this.weight;
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight() + "]";
	}

}