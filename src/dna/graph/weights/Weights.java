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
		case None:
			return Double.NaN;
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
}
