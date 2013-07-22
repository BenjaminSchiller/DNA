package dna.graph.directed;

import dna.graph.WeightedNode;
import dna.io.etc.Keywords;

public class DirectedNodeHsWeighted extends DirectedNodeHs implements
		WeightedNode {

	public DirectedNodeHsWeighted(int index) {
		super(index);
		this.weight = 0;
	}

	public DirectedNodeHsWeighted(String str) {
		super(str.split(Keywords.nodeWeightDelimiter)[0]);
		if (str.contains(Keywords.nodeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.nodeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}

	public DirectedNodeHsWeighted(int index, double weight) {
		super(index);
		this.weight = weight;
	}

	public String toString() {
		return super.toString() + "(" + this.weight + ")";
	}

	private double weight;

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.nodeWeightDelimiter
				+ this.weight;
	}

}
