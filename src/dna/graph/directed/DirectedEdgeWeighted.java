package dna.graph.directed;

import dna.graph.WeightedEdge;

public class DirectedEdgeWeighted extends DirectedEdge implements WeightedEdge {

	public DirectedEdgeWeighted(DirectedNode src, DirectedNode dst) {
		this(src, dst, 0);
	}

	public DirectedEdgeWeighted(DirectedNode src, DirectedNode dst,
			double weight) {
		super(src, dst);
		this.weight = weight;
	}

	public String toString() {
		return this.src.getIndex() + " -" + this.weight + "-> "
				+ this.dst.getIndex();
	}

	private double weight;

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
