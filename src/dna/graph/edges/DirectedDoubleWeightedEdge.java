package dna.graph.edges;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.IDoubleWeighted;
import dna.util.Config;

public class DirectedDoubleWeightedEdge extends DirectedEdge implements
		IWeightedEdge<Double>, IDoubleWeighted {
	private double weight;

	public DirectedDoubleWeightedEdge(DirectedNode src, DirectedNode dst,
			Double weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public DirectedDoubleWeightedEdge(String str, Graph g) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			this.weight = Double.parseDouble(str.split(Config
					.get("EDGE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
		this.setWeight(weight);
	}

	public DirectedDoubleWeightedEdge(String str, Graph g,
			HashMap<Integer, Node> addedNodes) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g, addedNodes);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			this.weight = Double.parseDouble(str.split(Config
					.get("EDGE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
		this.setWeight(weight);
	}

	public DirectedDoubleWeightedEdge(DirectedNode src, DirectedNode dst) {
		this(src, dst, 0d);
	}

	public String getStringRepresentation() {
		return super.getStringRepresentation()
				+ Config.get("EDGE_WEIGHT_DELIMITER") + this.weight;
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
