package dna.graph.weights;

import dna.util.Log;
import dna.util.Rand;

/**
 * 
 * weight holding a single double value.
 * 
 * @author benni
 * 
 */
public class DoubleWeight extends Weight {

	private double weight;

	public DoubleWeight(double weight) {
		this.weight = weight;
	}

	public DoubleWeight() {
		this(0);
	}

	public DoubleWeight(String str) {
		this.weight = Double.parseDouble(str);
	}

	public DoubleWeight(WeightSelection ws) {
		this.weight = DoubleWeight.getDoubleWeight(ws);
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String asString() {
		return Double.toString(this.weight);
	}

	@Override
	public Object getWeight() {
		return new double[] { weight };
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
