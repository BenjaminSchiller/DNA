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

	public DirectedEdgeWeighted(String str, DirectedGraph g) {
		super(str.split(Keywords.edgeWeightDelimiter)[0], g);
		if (str.contains(Keywords.edgeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.edgeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
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
