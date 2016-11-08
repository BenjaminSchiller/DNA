package dna.graph.weights.network;

import dna.graph.weights.Weight;

public class NetworkEdgeWeight extends Weight {

	protected double[] weights;

	public NetworkEdgeWeight(double[] weights) {
		this.weights = weights;
	}

	public double[] getWeights() {
		return weights;
	}

	public double getWeight(int index) {
		return weights[index];
	}

	@Override
	public String asString() {
		String buff = "";
		if (this.weights.length > 0)
			buff += this.weights[0];
		else
			buff = "none";
		for (int i = 1; i < this.weights.length; i++)
			buff += ", " + this.weights[i];
		return buff;
	}

	public NetworkEdgeWeight(WeightSelection ws) {
		this(new double[0]);
	}
}
