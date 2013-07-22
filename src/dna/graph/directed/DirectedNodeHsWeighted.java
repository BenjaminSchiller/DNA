package dna.graph.directed;

import dna.graph.WeightedNode;

public class DirectedNodeHsWeighted extends DirectedNodeHs implements
		WeightedNode {

	public DirectedNodeHsWeighted(int index) {
		this(index, 0);
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

}
