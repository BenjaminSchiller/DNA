package dna.graph.weights;

import dna.util.Rand;

public class Weights {
	public static enum DoubleWeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3
	}

	public static enum DoubleEdgeWeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3
	}

	public static enum DoubleNodeWeightSelection {
		None, NaN, One, Zero, Rand, RandTrim1, RandTrim2, RandTrim3
	}

	public static double getDoubleWeight(DoubleEdgeWeightSelection selection) {
		return getDoubleWeight(DoubleWeightSelection.valueOf(selection
				.toString()));
	}

	public static double getDoubleWeight(DoubleNodeWeightSelection selection) {
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
		None, Min, Max, One, Zero, Rand, RandPos, RandNeg
	}

	public static enum IntEdgeWeightSelection {
		None, Min, Max, One, Zero, Rand, RandPos, RandNeg
	}

	public static enum IntNodeWeightSelection {
		None, Min, Max, One, Zero, Rand, RandPos, RandNeg
	}

	public static int getIntWeight(IntEdgeWeightSelection selection) {
		return getIntWeight(IntWeightSelection.valueOf(selection.toString()));
	}

	public static int getIntWeight(IntNodeWeightSelection selection) {
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
		default:
			return Integer.MIN_VALUE;
		}
	}
}
