package dna.graph.weightsNew;

import dna.util.Log;
import dna.util.Rand;

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
	 * delimiter to separate the weight type from the actual weightm´, e.g.,
	 * D:0.24, I3:214;572;2
	 */
	public static final String WeightTypeDelimiter = ":";

	/**
	 * keyword to separate the different parts of a single weight, e.g. 14;2;52,
	 * 1.4;0.253;25.0
	 */
	public static final String WeightSeparator = ";";

	/**
	 * types of weights, used as keywords in String representation. each type
	 * has a separate class implementing the type, e.g., D2 => Double2dWeight, I
	 * => IntWeight
	 */
	public static enum WeightType {
		D, D2, D3, I, I2, I3
	};

	/**
	 * types that describe how a new weight is selected. some types can only be
	 * used for int or double, some for both.
	 * 
	 */
	public static enum WeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3, Min, Max, RandPos, RandNeg, RandPos100, RandPos10
	}

	public String toString() {
		return "(W) " + this.asString();
	}

	/**
	 * format: ${WeightType}:${Weight}, e.g., I:13, D2:6.2;2.4
	 * 
	 * @return String representation of the weight that can be parsed using the
	 *         fromString(String str) method.
	 */
	public String asString() {
		return this.getWeightType() + Weight.WeightTypeDelimiter
				+ this.asString_();
	}

	/**
	 * 
	 * @return String representation of this weight type's weight component that
	 *         can be parsed and interpreted by this weight's
	 *         String-constructor.
	 */
	protected abstract String asString_();

	/**
	 * 
	 * @return weight type of this weight.
	 */
	public abstract WeightType getWeightType();

	/**
	 * 
	 * @param str
	 *            String representation of a weight as given by the asString()
	 *            method
	 * @return weight object as specified by the type and weight component of
	 *         the given String; null in case the type is not supported (yet)
	 */
	public static Weight fromString(String str) {
		String[] temp = str.split(Weight.WeightTypeDelimiter);
		WeightType t = WeightType.valueOf(temp[0]);
		switch (t) {
		case D:
			return new DoubleWeight(temp[1]);
		case D2:
			return new Double2dWeight(temp[1]);
		case D3:
			return new Double3dWeight(temp[1]);
		case I:
			return new IntWeight(temp[1]);
		case I2:
			return new Int2dWeight(temp[1]);
		case I3:
			return new Int3dWeight(temp[1]);
		default:
			return null;
		}
	}

	/**
	 * generates a weight for the given type based on the given weight
	 * selection. it uses the getDoubleWeight and getIntWeight methods,
	 * depending on the weight type. this method can be used to generate random
	 * weights as specified by the weight selection.
	 * 
	 * @param t
	 *            type of the weight to be generated and returned
	 * @param sel
	 *            selection that defines how the components of the weight are to
	 *            be generated
	 * @return generated weight
	 */
	public static Weight getWeight(WeightType t, WeightSelection sel) {
		double x_d, y_d, z_d;
		int x_i, y_i, z_i;

		switch (t) {
		case D:
			x_d = Weight.getDoubleWeight(sel);
			return new DoubleWeight(x_d);
		case D2:
			x_d = Weight.getDoubleWeight(sel);
			y_d = Weight.getDoubleWeight(sel);
			return new Double2dWeight(x_d, y_d);
		case D3:
			x_d = Weight.getDoubleWeight(sel);
			y_d = Weight.getDoubleWeight(sel);
			z_d = Weight.getDoubleWeight(sel);
			return new Double3dWeight(x_d, y_d, z_d);
		case I:
			x_i = Weight.getIntWeight(sel);
			return new IntWeight(x_i);
		case I2:
			x_i = Weight.getIntWeight(sel);
			y_i = Weight.getIntWeight(sel);
			return new Int2dWeight(x_i, y_i);
		case I3:
			x_i = Weight.getIntWeight(sel);
			y_i = Weight.getIntWeight(sel);
			z_i = Weight.getIntWeight(sel);
			return new Int3dWeight(x_i, y_i, z_i);
		default:
			Log.warn("using unknown weight type '" + t + "'");
			return null;
		}
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
