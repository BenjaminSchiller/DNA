package dna.graph.nodes;

import java.util.Arrays;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IDoubleArrayWeighted;
import dna.util.Config;

public class UndirectedDoubleArrayWeightedNode extends UndirectedNode implements
		IWeightedNode<double[]>, IDoubleArrayWeighted {

	private double[] weight;

	public UndirectedDoubleArrayWeightedNode(int index, double[] weight,
			GraphDataStructure gds) {
		super(index, gds);
		this.setWeight(weight);
	}

	public UndirectedDoubleArrayWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Config.get("NODE_WEIGHT_DELIMITER"))[0], gds);
		if (str.contains(Config.get("NODE_WEIGHT_DELIMITER"))) {
			String[] v = str.split(Config.get("NODE_WEIGHT_DELIMITER"))[1]
					.split(Config.get("NODE_WEIGHT_SEPARATOR"));
			this.weight = new double[v.length];
			for (int i = 0; i < v.length; i++) {
				this.weight[i] = Double.parseDouble(v[i]);
			}
		} else {
			this.weight = new double[0];
		}
	}

	public UndirectedDoubleArrayWeightedNode(int index, GraphDataStructure gds) {
		super(index, gds);
		this.setWeight(new double[0]);
	}

	@Override
	public void setWeight(double[] newWeight) {
		this.weight = newWeight;
	}

	@Override
	public double[] getWeight() {
		return this.weight;
	}

	@Override
	public String getStringRepresentation() {
		if (this.weight.length == 0) {
			return super.getStringRepresentation();
		}
		StringBuffer buff = new StringBuffer(super.getStringRepresentation());
		buff.append(Config.get("NODE_WEIGHT_DELIMITER"));
		for (int i = 0; i < this.weight.length; i++) {
			if (i > 0) {
				buff.append(Config.get("NODE_WEIGHT_SEPARATOR"));
			}
			buff.append(this.weight[i]);
		}
		return buff.toString();
	}

	public String toString() {
		return super.toString() + " " + Arrays.toString(this.weight);
	}

}
