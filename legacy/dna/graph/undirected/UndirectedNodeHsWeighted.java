package dna.graph.undirected;

import dna.graph.WeightedNode;
import dna.io.etc.Keywords;

public class UndirectedNodeHsWeighted extends UndirectedNodeHs implements
		WeightedNode {

	protected double weight;

	public UndirectedNodeHsWeighted(int index) {
		super(index);
		this.weight = 0;
	}

	public UndirectedNodeHsWeighted(int index, double weight) {
		super(index);
		this.weight = weight;
	}

	public UndirectedNodeHsWeighted(String str) {
		super(str.split(Keywords.nodeWeightDelimiter)[0]);
		if (str.contains(Keywords.nodeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.nodeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}

	@Override
	public double getWeight() {
		return this.weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.nodeWeightDelimiter
				+ this.weight;
	}

}
