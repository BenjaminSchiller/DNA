package dna.graph.edges;

import dna.graph.Graph;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IDoubleWeighted;
import dna.io.etc.Keywords;

public class UndirectedDoubleWeightedEdge extends UndirectedEdge implements
		IWeightedEdge<Double>, IDoubleWeighted {
	private double weight;

	public UndirectedDoubleWeightedEdge(UndirectedNode src, UndirectedNode dst,
			Double weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public UndirectedDoubleWeightedEdge(String str, Graph g) {
		super(str.split(Keywords.edgeWeightDelimiter)[0], g);
		if (str.contains(Keywords.edgeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.edgeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}

	public UndirectedDoubleWeightedEdge(UndirectedNode src, UndirectedNode dst) {
		this(src, dst, 0d);
	}

	@Override
	public void setWeight(Double newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Double getWeight() {
		return this.weight;
	}

	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.edgeWeightDelimiter
				+ this.weight;
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight() + "]";
	}

}