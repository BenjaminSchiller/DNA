package dna.graph.edges;

import dna.graph.Graph;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IIntWeighted;
import dna.util.Config;

public class UndirectedIntWeightedEdge extends UndirectedEdge implements
		IWeightedEdge<Integer>, IIntWeighted {
	private int weight;

	public UndirectedIntWeightedEdge(UndirectedNode src, UndirectedNode dst,
			Integer weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public UndirectedIntWeightedEdge(String str, Graph g) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			this.weight = Integer.parseInt(str.split(Config
					.get("EDGE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
	}

	public UndirectedIntWeightedEdge(UndirectedNode src, UndirectedNode dst) {
		this(src, dst, 0);
	}

	@Override
	public void setWeight(Integer newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Integer getWeight() {
		return this.weight;
	}

	public String getStringRepresentation() {
		return super.getStringRepresentation()
				+ Config.get("EDGE_WEIGHT_DELIMITER") + this.weight;
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight() + "]";
	}

}