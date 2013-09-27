package dna.graph.edges;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.weights.IIntWeighted;
import dna.io.etc.Keywords;

public class DirectedIntWeightedEdge extends DirectedEdge implements
		IWeightedEdge<Integer>, IIntWeighted {
	private int weight;

	public DirectedIntWeightedEdge(DirectedNode src, DirectedNode dst,
			Integer weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public DirectedIntWeightedEdge(String str, Graph g) {
		super(str.split(Keywords.edgeWeightDelimiter)[0], g);
		if (str.contains(Keywords.edgeWeightDelimiter)) {
			this.weight = Integer.parseInt(str
					.split(Keywords.edgeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
		this.setWeight(weight);
	}

	public DirectedIntWeightedEdge(DirectedNode src, DirectedNode dst) {
		this(src, dst, 0);
	}

	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.edgeWeightDelimiter
				+ this.weight;
	}

	@Override
	public void setWeight(Integer newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Integer getWeight() {
		return this.weight;
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight() + "]";
	}

}
