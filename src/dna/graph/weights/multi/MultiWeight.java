package dna.graph.weights.multi;

import dna.graph.weights.Weight;

public class MultiWeight extends Weight {

	protected Weight[] weights;

	public MultiWeight(Weight[] weights) {
		this.weights = weights;
	}

	public Weight[] getWeights() {
		return weights;
	}

	public Weight getWeight(int index) {
		return weights[index];
	}

	public void setWeights(Weight[] weights) {
		this.weights = weights;
	}

	public MultiWeight(WeightSelection ws) {
		this(new Weight[0]);
	}

	@Override
	public String asString() {
		String buff = weights[0].toString();
		for (int i = 1; i < weights.length; i++)
			buff += Weight.WeightSeparator + weights[i].toString();
		return buff;
	}

}
