package dna.graph.nodes;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IDoubleWeighted;
import dna.util.Config;

public class UndirectedDoubleWeightedNode extends UndirectedNode implements
		IWeightedNode<Double>, IDoubleWeighted {
	private double weight;

	public UndirectedDoubleWeightedNode(int i, Double weight,
			GraphDataStructure gds) {
		super(i, gds);
		this.setWeight(weight);
	}

	public UndirectedDoubleWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Config.get("NODE_WEIGHT_DELIMITER"))[0], gds);
		if (str.contains(Config.get("NODE_WEIGHT_DELIMITER"))) {
			this.weight = Double.parseDouble(str.split(Config
					.get("NODE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
	}

	public UndirectedDoubleWeightedNode(int i, GraphDataStructure gds) {
		this(i, 0d, gds);
	}

	@Override
	public void setWeight(Double newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Double getWeight() {
		return this.weight;
	}

	@Override
	public String getStringRepresentation() {
		return super.getStringRepresentation()
				+ Config.get("NODE_WEIGHT_DELIMITER") + this.weight;
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight() + "]";
	}

}