package dna.graph.weights;

import dna.util.Log;
import dna.util.Rand;

/**
 * 
 * weight holding a single long value.
 * 
 * @author benni
 * 
 */
public class LongWeight extends Weight {

	private long weight;

	public LongWeight(long weight) {
		this.weight = weight;
	}

	public LongWeight() {
		this(0);
	}

	public LongWeight(String str) {
		this.weight = Long.parseLong(str);
	}

	public LongWeight(WeightSelection ws) {
		this.weight = LongWeight.getLongWeight(ws);
	}

	@Override
	public String asString() {
		return Long.toString(this.weight);
	}

	/**
	 * 
	 * generates and returns a long value as (part of) a new weight depending on
	 * the given selection
	 * 
	 * @param selection
	 *            weight selection
	 * @return long value as (part of) a new weight; Long.MIN_VALUE in case the
	 *         selection is not applicable to long weights
	 */
	public static long getLongWeight(WeightSelection selection) {
		switch (selection) {
		case Max:
			return Long.MAX_VALUE;
		case Min:
			return Long.MIN_VALUE;
		case One:
			return 1;
		case Rand:
			return Rand.rand.nextLong();
		case RandNeg:
			return Math.abs(Rand.rand.nextLong()) * -1;
		case RandPos:
			return Math.abs(Rand.rand.nextLong());
		case Zero:
			return 0;
		default:
			Log.warn("using non-long weight selection '" + selection + "'");
			return Long.MIN_VALUE;
		}
	}

}
