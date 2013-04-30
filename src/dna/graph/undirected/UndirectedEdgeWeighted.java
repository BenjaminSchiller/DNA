package dna.graph.undirected;

import dna.graph.WeightedEdge;

public class UndirectedEdgeWeighted extends UndirectedEdge implements
		WeightedEdge {

	private double weight;

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public UndirectedEdgeWeighted(UndirectedNode node1, UndirectedNode node2) {
		this(node1, node2, 0);
	}

	public UndirectedEdgeWeighted(UndirectedNode node1, UndirectedNode node2,
			double weight) {
		super(node1, node2);
		this.weight = weight;
	}

}
