package dna.util;

import java.util.Arrays;
import java.util.Set;

public class ArrayUtils {
	public static int[] incr(int[] values, int index) {
		try {
			values[index]++;
			return values;
		} catch (ArrayIndexOutOfBoundsException e) {
			int[] valuesNew = new int[index + 1];
			System.arraycopy(values, 0, valuesNew, 0, values.length);
			valuesNew[index] = 1;
			return valuesNew;
		}
	}

	public static double[] incr(double[] values, int index) {
		try {
			values[index]++;
			return values;
		} catch (ArrayIndexOutOfBoundsException e) {
			double[] valuesNew = new double[index + 1];
			System.arraycopy(values, 0, valuesNew, 0, values.length);
			valuesNew[index] = 1;
			return valuesNew;
		}
	}

	public static int[] decr(int[] values, int index) {
		try {
			values[index]--;
			return values;
		} catch (ArrayIndexOutOfBoundsException e) {
			int[] valuesNew = new int[index + 1];
			System.arraycopy(values, 0, valuesNew, 0, values.length);
			valuesNew[index] = -1;
			return valuesNew;
		}
	}

	public static double[] set(double[] values, int index, double value) {
		try {
			values[index] = value;
			return values;
		} catch (ArrayIndexOutOfBoundsException e) {
			double[] valuesNew = new double[index + 1];
			System.arraycopy(values, 0, valuesNew, 0, values.length);
			valuesNew[index] = value;
			return valuesNew;
		}
	}

	public static void divide(double[] values, double by) {
		for (int i = 0; i < values.length; i++) {
			values[i] /= by;
		}
	}

	/**
	 * truncates the given array by removing all fields at the end that contain
	 * the given value, e.g., {0,1,0,2,3,0,0} => {0,1,0,2,3} for value=0
	 * 
	 * @param values
	 *            array to truncate
	 * @param value
	 *            value to remove from the end of the array
	 * @return truncated array
	 */
	public static int[] truncate(int[] values, int value) {
		if (values[values.length - 1] != value) {
			return values;
		}
		int index = values.length - 1;
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i] != value) {
				break;
			}
			index--;
		}
		int[] valuesNew = new int[index + 1];
		System.arraycopy(values, 0, valuesNew, 0, index + 1);
		return valuesNew;
	}

	/**
	 * truncates the given array by removing all fields at the end that contain
	 * the given value, e.g., {0,1,0,2,3,0,0} => {0,1,0,2,3} for value=0
	 * 
	 * @param values
	 *            array to truncate
	 * @param value
	 *            value to remove from the end of the array
	 * @return truncated array
	 */
	public static double[] truncate(double[] values, double value) {
		if (values[values.length - 1] != value) {
			return values;
		}
		int index = values.length - 1;
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i] != value) {
				break;
			}
			index--;
		}
		double[] valuesNew = new double[index + 1];
		System.arraycopy(values, 0, valuesNew, 0, index + 1);
		return valuesNew;
	}

	public static int sum(int[] values) {
		int sum = 0;
		for (int v : values) {
			sum += v;
		}
		return sum;
	}

	public static double sum(double[] values) {
		double sum = 0;
		for (double v : values) {
			sum += v;
		}
		return sum;
	}

	public static String toString(int[] values) {
		StringBuffer buff = new StringBuffer();
		for (int v : values) {
			buff.append(v + " ");
		}
		return buff.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String toString(Set set) {
		StringBuffer buff = new StringBuffer();
		for (Object obj : set) {
			buff.append(obj.toString() + " ");
		}
		return buff.toString();
	}

	public static boolean equals(int[] v1, int[] v2) {
		if (v1.length != v2.length) {
			return false;
		}
		for (int i = 0; i < v1.length; i++) {
			if (v1[i] != v2[i]) {
				return false;
			}
		}
		return true;
	}

	public static double avg(double[] values) {
		double avg = 0;
		for (double v : values) {
			avg += v;
		}
		return avg / values.length;
	}

	public static double med(double[] values) {
		Arrays.sort(values);
		return values[values.length / 2];
	}

	/**
	 * 
	 * @param v1
	 *            arrays of values
	 * @param v2
	 *            array of values to compare arrays are not equal
	 * @return true if both arrays have the same length and all values are equal
	 */
	public static boolean equals(double[] v1, double[] v2) {
		return ArrayUtils.equals(v1, v2, null);
	}

	/**
	 * 
	 * @param v1
	 *            arrays of values
	 * @param v2
	 *            array of values to compare
	 * @param name
	 *            if a name is given, debug log output is printed in case the
	 *            arrays are not equal
	 * @return true if both arrays have the same length and all values are equal
	 */
	public static boolean equals(double[] v1, double[] v2, String name) {
		if (v1.length != v2.length) {
			if (name != null) {
				Log.warn(name + " - length differs: " + v1.length + " != "
						+ v2.length);
			}
			return false;
		}
		for (int i = 0; i < v1.length; i++) {
			if (v1[i] != v2[i]) {
				if (name != null) {
					Log.warn(name + " - values @ index " + i + " differs: "
							+ v1[i] + " != " + v2[i]);
				}
				return false;
			}
		}
		return true;
	}
}
