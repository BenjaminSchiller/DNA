package dna.graph.weights;

import dna.util.Rand;

public class Weights {

	public static final String DoubleWeightPrefix = "D_";

	public static final String IntWeightPrefix = "I_";

	public static enum WeightSelection {
		None, D_NaN, D_One, D_Zero, D_Rand, D_RandTrim1, D_RandTrim2, D_RandTrim3, I_Min, I_Max, I_One, I_Zero, I_Rand, I_RandPos, I_RandNeg, I_RandPos100, I_RandPos10
	}

	public static enum NodeWeightSelection {
		None, D_NaN, D_One, D_Zero, D_Rand, D_RandTrim1, D_RandTrim2, D_RandTrim3, I_Min, I_Max, I_One, I_Zero, I_Rand, I_RandPos, I_RandNeg, I_RandPos100, I_RandPos10
	}

	public static enum EdgeWeightSelection {
		None, D_NaN, D_One, D_Zero, D_Rand, D_RandTrim1, D_RandTrim2, D_RandTrim3, I_Min, I_Max, I_One, I_Zero, I_Rand, I_RandPos, I_RandNeg, I_RandPos100, I_RandPos10
	}

	public static Object getWeight(NodeWeightSelection selection) {
		return getWeight(WeightSelection.valueOf(selection.toString()));
	}

	public static Object getWeight(EdgeWeightSelection selection) {
		return getWeight(WeightSelection.valueOf(selection.toString()));
	}

	public static Object getWeight(WeightSelection selection) {
		if (selection.toString().startsWith(DoubleWeightPrefix)) {
			return getDoubleWeight(DoubleWeightSelection.valueOf(selection
					.toString().replaceFirst(DoubleWeightPrefix, "")));
		} else if (selection.toString().startsWith(IntWeightPrefix)) {
			return getIntWeight(IntWeightSelection.valueOf(selection.toString()
					.replaceFirst(IntWeightPrefix, "")));
		} else {
			return null;
		}
	}

	public static enum DoubleWeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3
	}

	public static enum DoubleNodeWeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3
	}

	public static enum DoubleEdgeWeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3
	}

	public static double getDoubleWeight(DoubleNodeWeightSelection selection) {
		return getDoubleWeight(DoubleWeightSelection.valueOf(selection
				.toString()));
	}

	public static double getDoubleWeight(DoubleEdgeWeightSelection selection) {
		return getDoubleWeight(DoubleWeightSelection.valueOf(selection
				.toString()));
	}

	public static double getDoubleWeight(DoubleWeightSelection selection) {
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
			return Double.NaN;
		}
	}

	public static enum IntWeightSelection {
		None, Min, Max, One, Zero, Rand, RandPos, RandNeg, RandPos100, RandPos10
	}

	public static enum IntNodeWeightSelection {
		None, Min, Max, One, Zero, Rand, RandPos, RandNeg, RandPos100, RandPos10
	}

	public static enum IntEdgeWeightSelection {
		None, Min, Max, One, Zero, Rand, RandPos, RandNeg, RandPos100, RandPos10
	}

	public static int getIntWeight(IntNodeWeightSelection selection) {
		return getIntWeight(IntWeightSelection.valueOf(selection.toString()));
	}

	public static int getIntWeight(IntEdgeWeightSelection selection) {
		return getIntWeight(IntWeightSelection.valueOf(selection.toString()));
	}

	public static int getIntWeight(IntWeightSelection selection) {
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
			return Rand.rand.nextInt(100);
		case RandPos10:
			return Rand.rand.nextInt(10);
		default:
			return Integer.MIN_VALUE;
		}
	}
}
