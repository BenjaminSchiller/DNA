package dna.graph.weights.network;

import dna.graph.weights.TypedWeight;
import dna.graph.weights.Weight;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.graph.weights.multi.MultiWeight;
import dna.graph.weights.network.NetworkWeight.ElementType;

public class NetworkMultiWeight extends MultiWeight {

	public NetworkMultiWeight() {
		super(new Weight[] { new TypedWeight(ElementType.UNKNOWN.toString()),
				new DoubleWeight() });
	}

	public NetworkMultiWeight(TypedWeight type, double[] w) {
		super(new Weight[w.length + 1]);
		weights[0] = type;
		for (int i = 0; i < w.length; i++) {
			weights[i + 1] = new DoubleWeight(w[i]);
		}
	}

	public NetworkMultiWeight(TypedWeight type, DoubleWeight[] w) {
		super(new Weight[w.length + 1]);
		weights[0] = type;
		for (int i = 0; i < w.length; i++)
			weights[i + 1] = w[i];
	}

	public void setType(TypedWeight type) {
		weights[0] = type;
	}

	public TypedWeight getType() {
		return (TypedWeight) weights[0];
	}

	public DoubleWeight[] getWeights() {
		DoubleWeight[] ws = new DoubleWeight[this.weights.length - 1];
		for (int i = 1; i < this.weights.length; i++) {
			ws[i - 1] = (DoubleWeight) this.weights[i];
		}
		return ws;
	}

	public double[] getDoubles() {
		double[] doubles = new double[this.weights.length - 1];
		for (int i = 1; i < this.weights.length; i++) {
			doubles[i - 1] = ((DoubleWeight) this.weights[i]).getWeight();
		}
		return doubles;
	}

	public NetworkMultiWeight(WeightSelection ws) {
		this();
	}

	public static NetworkMultiWeight addition(NetworkMultiWeight w1,
			double[] doubles) {
		TypedWeight type = w1.getType();
		DoubleWeight[] weights1 = w1.getWeights();

		DoubleWeight[] weightsNew = new DoubleWeight[Math.max(weights1.length,
				doubles.length)];

		for (int i = 0; i < weightsNew.length; i++) {
			double d1 = (i < weights1.length ? weights1[i].getWeight() : 0.0);
			double d2 = (i < doubles.length ? doubles[i] : 0.0);
			weightsNew[i] = new DoubleWeight(d1 + d2);
		}

		return new NetworkMultiWeight(type, weightsNew);
	}

	public static NetworkMultiWeight addition(NetworkMultiWeight w1,
			NetworkMultiWeight w2) {
		TypedWeight type = w1.getType();
		DoubleWeight[] weights1 = w1.getWeights();
		DoubleWeight[] weights2 = w2.getWeights();

		DoubleWeight[] weightsNew = new DoubleWeight[Math.max(weights1.length,
				weights2.length)];

		for (int i = 0; i < weightsNew.length; i++) {
			double d1 = (i < weights1.length ? weights1[i].getWeight() : 0.0);
			double d2 = (i < weights2.length ? weights2[i].getWeight() : 0.0);
			weightsNew[i] = new DoubleWeight(d1 + d2);
		}

		return new NetworkMultiWeight(type, weightsNew);
	}
}
