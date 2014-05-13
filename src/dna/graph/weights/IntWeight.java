package dna.graph.weights;

import dna.util.Log;
import dna.util.Rand;

/**
 * 
 * weight holding a single int value.
 * 
 * @author benni
 * 
 */
public class IntWeight extends Weight {

	private int weight;

	public IntWeight(int weight) {
		this.weight = weight;
	}

	public IntWeight() {
		this(0);
	}

	public IntWeight(String str) {
		this.weight = Integer.parseInt(str);
	}

	public IntWeight(WeightSelection ws) {
		this.weight = IntWeight.getIntWeight(ws);
	}

	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String asString() {
		return Integer.toString(this.weight);
	}

	/**
	 * generates and returns an int value as (part of) a new weight depending on
	 * the given selection.
	 * 
	 * @param selection
	 *            weight selection
	 * @return int value as (part of) a new weight; Integer.MIN_VALUE in case
	 *         the selection is not applicable to int weights
	 */
	public static int getIntWeight(WeightSelection selection) {
		switch (selection) {
		case Max:
			return Integer.MAX_VALUE;
		case Min:
			return Integer.MIN_VALUE;
		case One:
			return 1;
		case Rand:
			return Rand.rand.nextInt();
		case RandPos:
			return Math.abs(Rand.rand.nextInt());
		case RandNeg:
			return Math.abs(Rand.rand.nextInt()) * -1;
		case Zero:
			return 0;
		case RandPos100:
			return Rand.rand.nextInt(100) + 1;
		case RandPos10:
			return Rand.rand.nextInt(10) + 1;
		default:
			Log.warn("using non-int weight selection '" + selection + "'");
			return Integer.MIN_VALUE;
		}
	}

}
