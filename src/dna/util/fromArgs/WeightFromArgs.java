package dna.util.fromArgs;

import dna.graph.weights.Double2dWeight;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.Int2dWeight;
import dna.graph.weights.Int3dWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Long2dWeight;
import dna.graph.weights.Long3dWeight;
import dna.graph.weights.LongWeight;
import dna.graph.weights.Weight;

public class WeightFromArgs {
	public static enum WeightType {
		Double, Double2d, Double3d, Int, Int2d, Int3d, Long, Long2d, Long3d
	}

	public static Class<? extends Weight> parse(String weightType) {
		return parse(WeightType.valueOf(weightType));
	}

	public static Class<? extends Weight> parse(WeightType weightType) {
		switch (weightType) {
		case Double:
			return DoubleWeight.class;
		case Double2d:
			return Double2dWeight.class;
		case Double3d:
			return Double3dWeight.class;
		case Int:
			return IntWeight.class;
		case Int2d:
			return Int2dWeight.class;
		case Int3d:
			return Int3dWeight.class;
		case Long:
			return LongWeight.class;
		case Long2d:
			return Long2dWeight.class;
		case Long3d:
			return Long3dWeight.class;
		default:
			throw new IllegalArgumentException("unknown weight type: "
					+ weightType);
		}
	}
}
