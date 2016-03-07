package dna.graph.weights.distances;

import dna.graph.weights.Weight;
import dna.graph.weights.doubleW.Double2dWeight;
import dna.graph.weights.doubleW.Double3dWeight;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.graph.weights.intW.Int2dWeight;
import dna.graph.weights.intW.Int3dWeight;
import dna.graph.weights.intW.IntWeight;
import dna.graph.weights.longW.Long2dWeight;
import dna.graph.weights.longW.Long3dWeight;
import dna.graph.weights.longW.LongWeight;
import dna.util.Log;

/**
 * 
 * Provides static methods to compute the Euclidean distance between two weights
 * (int ,double, long of abitrary number of dimensions).
 * http://en.wikipedia.org/wiki/Euclidean_distance
 * 
 * @author benni
 * 
 */
public class EuclideanDistance {

	public static double distSquared(double[] a, double[] b) {
		double dist = 0;
		for (int i = 0; i < a.length; i++) {
			double d = a[i] - b[i];
			dist += d * d;
		}
		return dist;
	}

	public static double dist(double[] a, double[] b) {
		return Math.sqrt(distSquared(a, b));
	}

	public static double dist(Weight w1, Weight w2) {
		if (!(w1.getClass().equals(w2.getClass()))) {
			Log.warn("trying to compute the distance between two different weights: "
					+ w1.getClass() + " AND  " + w2.getClass());
			return Double.NaN;
		}
		if (w1 instanceof DoubleWeight) {
			return dist((DoubleWeight) w1, (DoubleWeight) w2);
		}
		if (w1 instanceof Double2dWeight) {
			return dist((Double2dWeight) w1, (Double2dWeight) w2);
		}
		if (w1 instanceof Double3dWeight) {
			return dist((Double3dWeight) w1, (Double3dWeight) w2);
		}
		if (w1 instanceof IntWeight) {
			return dist((IntWeight) w1, (IntWeight) w2);
		}
		if (w1 instanceof Int2dWeight) {
			return dist((Int2dWeight) w1, (Int2dWeight) w2);
		}
		if (w1 instanceof Int3dWeight) {
			return dist((Int3dWeight) w1, (Int3dWeight) w2);
		}
		if (w1 instanceof LongWeight) {
			return dist((LongWeight) w1, (LongWeight) w2);
		}
		if (w1 instanceof Long2dWeight) {
			return dist((Long2dWeight) w1, (Long2dWeight) w2);
		}
		if (w1 instanceof Long3dWeight) {
			return dist((Long3dWeight) w1, (Long3dWeight) w2);
		}
		Log.warn("trying to compute the distance of an unsupported weight: "
				+ w1.getClass());
		return Double.NaN;
	}

	public static double dist(DoubleWeight w1, DoubleWeight w2) {
		return Math.abs(w1.getWeight() - w2.getWeight());
	}

	public static double dist(Double2dWeight w1, Double2dWeight w2) {
		return dist(new double[] { w1.getX(), w1.getY() },
				new double[] { w2.getX(), w2.getY() });
	}

	public static double dist(Double3dWeight w1, Double3dWeight w2) {
		return dist(new double[] { w1.getX(), w1.getY(), w1.getZ() },
				new double[] { w2.getX(), w2.getY(), w2.getZ() });
	}

	public static double dist(IntWeight w1, IntWeight w2) {
		return Math.abs(w1.getWeight() - w2.getWeight());
	}

	public static double dist(Int2dWeight w1, Int2dWeight w2) {
		return dist(new double[] { w1.getX(), w1.getY() },
				new double[] { w2.getX(), w2.getY() });
	}

	public static double dist(Int3dWeight w1, Int3dWeight w2) {
		return dist(new double[] { w1.getX(), w1.getY(), w1.getZ() },
				new double[] { w2.getX(), w2.getY(), w2.getZ() });
	}

	public static double dist(LongWeight w1, LongWeight w2) {
		return Math.abs(w1.getWeight() - w2.getWeight());
	}

	public static double dist(Long2dWeight w1, Long2dWeight w2) {
		return dist(new double[] { w1.getX(), w1.getY() },
				new double[] { w2.getX(), w2.getY() });
	}

	public static double dist(Long3dWeight w1, Long3dWeight w2) {
		return dist(new double[] { w1.getX(), w1.getY(), w1.getZ() },
				new double[] { w2.getX(), w2.getY(), w2.getZ() });
	}
}
