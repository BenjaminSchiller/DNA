package dna.graph.nodes;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IIntWeighted;
import dna.util.Config;

public class DirectedIntWeightedNode extends DirectedNode implements
		IWeightedNode<Integer>, IIntWeighted {

	private int weight;

	public DirectedIntWeightedNode(int i, Integer weight, GraphDataStructure gds) {
		super(i, gds);
		this.setWeight(weight);
	}

	public DirectedIntWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Config.get("NODE_WEIGHT_DELIMITER"))[0], gds);
		if (str.contains(Config.get("NODE_WEIGHT_DELIMITER"))) {
			this.weight = Integer.parseInt(str.split(Config
					.get("NODE_WEIGHT_DELIMITER"))[1]);
		} else {
			this.weight = 0;
		}
	}

	public DirectedIntWeightedNode(int i, GraphDataStructure gds) {
		this(i, 0, gds);
	}

	@Override
	public void setWeight(Integer newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Integer getWeight() {
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
