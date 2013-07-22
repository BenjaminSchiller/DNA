package dna.graph.undirected;

import dna.graph.WeightedEdge;
import dna.io.etc.Keywords;

public class UndirectedEdgeWeighted extends UndirectedEdge implements
		WeightedEdge {

	public UndirectedEdgeWeighted(UndirectedNode node1, UndirectedNode node2) {
		this(node1, node2, 0);
	}

	public UndirectedEdgeWeighted(UndirectedNode node1, UndirectedNode node2,
			double weight) {
		super(node1, node2);
		this.weight = weight;
	}

	public UndirectedEdgeWeighted(String str, UndirectedGraph graph) {
		super(str.split(Keywords.edgeWeightDelimiter)[0], graph);
		if (str.contains(Keywords.edgeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.edgeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}

	/**
	 * 
	 * @return String representation of this edge
	 */
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.edgeWeightDelimiter
				+ this.weight;
	}

	private double weight;

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
