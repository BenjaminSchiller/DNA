package dna.graph.weights;

import dna.util.Rand;

public class Weights {

	public static final String DoubleWeightPrefix = "D_";

	public static final String Double2WeightPrefix = "D2_";

	public static final String Double3WeightPrefix = "D3_";

	public static final String IntWeightPrefix = "I_";

	public static final String Int2WeightPrefix = "I2_";

	public static final String Int3WeightPrefix = "I3_";

	public static enum WeightSelection {
		None, D_NaN, D_One, D_Zero, D_Rand, D_RandTrim1, D_RandTrim2, D_RandTrim3, D2_NaN, D2_One, D2_Zero, D2_Rand, D2_RandTrim1, D2_RandTrim2, D2_RandTrim3, D3_NaN, D3_One, D3_Zero, D3_Rand, D3_RandTrim1, D3_RandTrim2, D3_RandTrim3, I_Min, I_Max, I_One, I_Zero, I_Rand, I_RandPos, I_RandNeg, I_RandPos100, I_RandPos10, I2_Min, I2_Max, I2_One, I2_Zero, I2_Rand, I2_RandPos, I2_RandNeg, I2_RandPos100, I2_RandPos10, I3_Min, I3_Max, I3_One, I3_Zero, I3_Rand, I3_RandPos, I3_RandNeg, I3_RandPos100, I3_RandPos10
	}

	public static enum NodeWeightSelection {
		None, D_NaN, D_One, D_Zero, D_Rand, D_RandTrim1, D_RandTrim2, D_RandTrim3, D2_NaN, D2_One, D2_Zero, D2_Rand, D2_RandTrim1, D2_RandTrim2, D2_RandTrim3, D3_NaN, D3_One, D3_Zero, D3_Rand, D3_RandTrim1, D3_RandTrim2, D3_RandTrim3, I_Min, I_Max, I_One, I_Zero, I_Rand, I_RandPos, I_RandNeg, I_RandPos100, I_RandPos10, I2_Min, I2_Max, I2_One, I2_Zero, I2_Rand, I2_RandPos, I2_RandNeg, I2_RandPos100, I2_RandPos10, I3_Min, I3_Max, I3_One, I3_Zero, I3_Rand, I3_RandPos, I3_RandNeg, I3_RandPos100, I3_RandPos10
	}

	public static enum EdgeWeightSelection {
		None, D_NaN, D_One, D_Zero, D_Rand, D_RandTrim1, D_RandTrim2, D_RandTrim3, D2_NaN, D2_One, D2_Zero, D2_Rand, D2_RandTrim1, D2_RandTrim2, D2_RandTrim3, D3_NaN, D3_One, D3_Zero, D3_Rand, D3_RandTrim1, D3_RandTrim2, D3_RandTrim3, I_Min, I_Max, I_One, I_Zero, I_Rand, I_RandPos, I_RandNeg, I_RandPos100, I_RandPos10, I2_Min, I2_Max, I2_One, I2_Zero, I2_Rand, I2_RandPos, I2_RandNeg, I2_RandPos100, I2_RandPos10, I3_Min, I3_Max, I3_One, I3_Zero, I3_Rand, I3_RandPos, I3_RandNeg, I3_RandPos100, I3_RandPos10
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
		} else if (selection.toString().startsWith(Double2WeightPrefix)) {
			return new double[] {
					getDoubleWeight(DoubleWeightSelection.valueOf(selection
							.toString().replaceFirst(Double2WeightPrefix, ""))),
					getDoubleWeight(DoubleWeightSelection.valueOf(selection
							.toString().replaceFirst(Double2WeightPrefix, ""))) };
		} else if (selection.toString().startsWith(Double3WeightPrefix)) {
			return new double[] {
					getDoubleWeight(DoubleWeightSelection.valueOf(selection
							.toString().replaceFirst(Double3WeightPrefix, ""))),
					getDoubleWeight(DoubleWeightSelection.valueOf(selection
							.toString().replaceFirst(Double3WeightPrefix, ""))),
					getDoubleWeight(DoubleWeightSelection.valueOf(selection
							.toString().replaceFirst(Double3WeightPrefix, ""))) };
		} else if (selection.toString().startsWith(IntWeightPrefix)) {
			return getIntWeight(IntWeightSelection.valueOf(selection.toString()
					.replaceFirst(IntWeightPrefix, "")));
		} else if (selection.toString().startsWith(Int2WeightPrefix)) {
			return new int[] {
					getIntWeight(IntWeightSelection.valueOf(selection
							.toString().replaceFirst(Int2WeightPrefix, ""))),
					getIntWeight(IntWeightSelection.valueOf(selection
							.toString().replaceFirst(Int2WeightPrefix, ""))) };
		} else if (selection.toString().startsWith(Int3WeightPrefix)) {
			return new int[] {
					getIntWeight(IntWeightSelection.valueOf(selection
							.toString().replaceFirst(Int3WeightPrefix, ""))),
					getIntWeight(IntWeightSelection.valueOf(selection
							.toString().replaceFirst(Int3WeightPrefix, ""))),
					getIntWeight(IntWeightSelection.valueOf(selection
							.toString().replaceFirst(Int3WeightPrefix, ""))) };
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
			return Rand.rand.nextInt(100) + 1;
		case RandPos10:
			return Rand.rand.nextInt(10) + 1;
		default:
			return Integer.MIN_VALUE;
		}
	}
}
