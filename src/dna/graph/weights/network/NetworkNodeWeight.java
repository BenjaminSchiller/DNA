package dna.graph.weights.network;

import dna.graph.weights.TypedWeight;

public class NetworkNodeWeight extends TypedWeight {

	protected double[] weights;

	public NetworkNodeWeight(String type, double[] weights) {
		super(type);
		this.weights = weights;
	}

	public double[] getWeights() {
		return weights;
	}

	public double getWeight(int index) {
		return weights[index];
	}

	@Override
	public String toString() {
		String buff = this.getType();
		for (double d : this.weights)
			buff += ", " + d;
		return buff;
	}

	public NetworkNodeWeight(WeightSelection ws) {
		this("unknown", new double[0]);
	}

}
