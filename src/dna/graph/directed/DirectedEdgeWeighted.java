package dna.graph.directed;

import dna.graph.WeightedEdge;
import dna.io.etc.Keywords;

public class DirectedEdgeWeighted extends DirectedEdge implements WeightedEdge {

	public DirectedEdgeWeighted(DirectedNode src, DirectedNode dst) {
		this(src, dst, 0);
	}

	public DirectedEdgeWeighted(DirectedNode src, DirectedNode dst,
			double weight) {
		super(src, dst);
		this.weight = weight;
	}

	public DirectedEdgeWeighted(String s, DirectedGraph g) {
		super(s.split(Keywords.edgeWeightDelimiter)[0], g);
		this.weight = Double
				.parseDouble(s.split(Keywords.edgeWeightDelimiter)[1]);
	}

	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.edgeWeightDelimiter
				+ this.weight;
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
