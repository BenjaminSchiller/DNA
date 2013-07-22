package dna.graph.directed;

import dna.graph.WeightedNode;

public class DirectedNodeAlWeighted extends DirectedNodeAl implements
		WeightedNode {

	public DirectedNodeAlWeighted(int index) {
		this(index, 0);
	}

	public DirectedNodeAlWeighted(int index, double weight) {
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
