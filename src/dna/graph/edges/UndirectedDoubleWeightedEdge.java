package dna.graph.edges;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IDoubleWeighted;
import dna.util.Config;

public class UndirectedDoubleWeightedEdge extends UndirectedEdge implements
		IWeightedEdge<Double>, IDoubleWeighted {
	private double weight;

	public UndirectedDoubleWeightedEdge(UndirectedNode src, UndirectedNode dst,
			Double weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public UndirectedDoubleWeightedEdge(String str, Graph g) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			this.weight = Double.parseDouble(str.split(Config
					.get("EDGE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
	}

	public UndirectedDoubleWeightedEdge(String str, Graph g,
			HashMap<Integer, Node> addedNodes) {
		super(str.split(Config.get("EDGE_WEIGHT_DELIMITER"))[0], g, addedNodes);
		if (str.contains(Config.get("EDGE_WEIGHT_DELIMITER"))) {
			this.weight = Double.parseDouble(str.split(Config
					.get("EDGE_WEIGHT_DELIMITER"))[1]);
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
		return super.getStringRepresentation()
				+ Config.get("EDGE_WEIGHT_DELIMITER") + this.weight;
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight() + "]";
	}

}