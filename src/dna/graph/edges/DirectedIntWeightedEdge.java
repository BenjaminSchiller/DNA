package dna.graph.edges;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.IIntWeighted;
import dna.util.Config;

public class DirectedIntWeightedEdge extends DirectedEdge implements
		IWeightedEdge<Integer>, IIntWeighted {
	private int weight;

	public DirectedIntWeightedEdge(DirectedNode src, DirectedNode dst,
			Integer weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public DirectedIntWeightedEdge(String str, Graph g) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			this.weight = Integer.parseInt(str.split(Config
					.get("EDGE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
		this.setWeight(weight);
	}

	public DirectedIntWeightedEdge(String str, Graph g,
			HashMap<Integer, Node> addedNodes) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g, addedNodes);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			this.weight = Integer.parseInt(str.split(Config
					.get("EDGE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
		this.setWeight(weight);
	}

	public DirectedIntWeightedEdge(DirectedNode src, DirectedNode dst) {
		this(src, dst, 0);
	}

	public String getStringRepresentation() {
		return super.getStringRepresentation()
				+ Config.get("EDGE_WEIGHT_DELIMITER") + this.weight;
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