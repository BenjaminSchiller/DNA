package dna.graph.weights.network;

import dna.graph.weights.TypedWeight;

/**
 * A NetworkNodeWeight is like a DoubleMultiWeight which extends a TypedWeight.
 * It can contain an arbitrary number of doubles while also holding a specific
 * node-type.
 * 
 * @author Rwilmes
 * 
 */
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

	@Override
	public String asString() {
		return toString();
	}

	public NetworkNodeWeight(WeightSelection ws) {
		this("unknown", new double[0]);
	}

}
