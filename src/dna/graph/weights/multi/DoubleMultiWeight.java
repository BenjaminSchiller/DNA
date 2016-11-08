package dna.graph.weights.multi;

import dna.graph.weights.Weight;
import dna.util.Log;
import dna.util.Rand;

/**
 * The DoubleMultiWeight class represents a weight object containing an abitrary
 * number of double-weights. They can be accessed via their index.
 * 
 * @author Rwilmes
 * 
 */
public class DoubleMultiWeight extends Weight {

	protected double[] weights;

	public DoubleMultiWeight(double[] weights) {
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

	public DoubleMultiWeight(WeightSelection ws) {
		this.weights = new double[] { DoubleMultiWeight.getDoubleWeight(ws) };
	}

	/**
	 * generates and returns a double value as (part of) a new weight depending
	 * on the given selection.
	 * 
	 * @param selection
	 *            weight selection
	 * @return double value as (part of) a new weight; Double.NaN in case the
	 *         selection is not applicable to double weights
	 */
	public static double getDoubleWeight(WeightSelection selection) {
		switch (selection) {
		case NaN:
			return Double.NaN;
		case One:
			return 1.0;
		case Zero:
			return 0.0;
		case Rand:
			return Rand.rand.nextDouble();
		case RandTrim1:
			return (double) Math.round(Rand.rand.nextDouble() * 10) / 10.0;
		case RandTrim2:
			return (double) Math.round(Rand.rand.nextDouble() * 100.0) / 100.0;
		case RandTrim3:
			return (double) Math.round(Rand.rand.nextDouble() * 1000.0) / 1000.0;
		default:
			Log.warn("using non-double weight selection '" + selection + "'");
			return Double.NaN;
		}
	}
}
