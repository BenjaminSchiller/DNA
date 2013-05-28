package dna.graph.undirected;

import dna.graph.WeightedNode;
import dna.io.etc.Keywords;

public class UndirectedNodeAlWeighted extends UndirectedNodeAl implements
		WeightedNode {

	protected double weight;

	public UndirectedNodeAlWeighted(int index) {
		super(index);
		this.weight = 0;
	}

	public UndirectedNodeAlWeighted(int index, double weight) {
		super(index);
		this.weight = weight;
	}

	public UndirectedNodeAlWeighted(String str) {
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
