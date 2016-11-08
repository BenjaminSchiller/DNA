package dna.graph.weights.multi;

import dna.graph.weights.Weight;

/**
 * The MultiWeight class is a n-dimensional weight object, containing an
 * arbitrary number of Weight objects in its internal weights-array.
 * 
 * @author Rwilmes
 * 
 */
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

	/** Generates an empty array of Weight. **/
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
