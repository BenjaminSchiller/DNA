package dna.graph.edges;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.io.etc.Keywords;

public class DirectedDoubleWeightedEdge extends DirectedEdge implements
		IWeightedEdge<Double> {
	private double weight;

	public DirectedDoubleWeightedEdge(DirectedNode src, DirectedNode dst,
			Double weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public DirectedDoubleWeightedEdge(String str, Graph g) {
		super(str.split(Keywords.edgeWeightDelimiter)[0], g);
		if (str.contains(Keywords.edgeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.edgeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
		this.setWeight(weight);
	}

	public DirectedDoubleWeightedEdge(DirectedNode src, DirectedNode dst) {
		this(src, dst, 0d);
	}

	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.edgeWeightDelimiter
				+ this.weight;
	}

	@Override
	public void setWeight(Double newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Double getWeight() {
		return this.weight;
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight() + "]";
	}

}
