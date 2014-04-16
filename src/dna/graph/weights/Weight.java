package dna.graph.weights;

/**
 * 
 * this class implements an abstract class for representing node and edge
 * weights. it provides general methods for generating (random) weights of
 * different types. a String representation of an instance can be created using
 * the asString() method. it is required that each weight implementation offers
 * a constructor that takes a single String parameter (the output from the
 * asString() and parses the weight from that). using the static
 * fromString(String str) method, weights can be generated from their respective
 * String representation. for generating (randomized) weights, different
 * selections can be used, some of them applicable only to specific types (e.g.,
 * int, double) others applicable to all weight types available (so far).
 * 
 * @author benni
 * 
 */
public abstract class Weight {

	/**
	 * delimiter used to separate edge or node from weight, e.g., node:
	 * 1@$weight, edge: 1->2@$weight
	 */
	public static final String WeightDelimiter = "@";

	/**
	 * keyword to separate the different parts of a single weight, e.g. 14;2;52,
	 * 1.4;0.253;25.0
	 */
	public static final String WeightSeparator = ";";

	/**
	 * types that describe how a new weight is selected. some types can only be
	 * used for int or double, some for both.
	 * 
	 */
	public static enum WeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3, Min, Max, RandPos, RandNeg, RandPos100, RandPos10
	}

	public String toString() {
		return this.asString();
	}

	/**
	 * format: ${Weight}, e.g., 13, 6.2;2.4
	 * 
	 * @return String representation of the weight that can be parsed using the
	 *         fromString(String str) method.
	 */
	public abstract String asString();

	/**
	 * Get a condensed version of the weight
	 * 
	 * @return weight
	 */
	public abstract Object getWeight();

	/**
	 * Equality of weights
	 * 
	 * @return
	 */
	public boolean equals(Object o) {
		if (Weight.class.isAssignableFrom(o.getClass())) {
			Weight oCasted = (Weight) o;
			return oCasted.toString().equals(this.toString());
		}
		return false;
	}
}
