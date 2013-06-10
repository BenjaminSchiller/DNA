package dna.util;

public class DataUtils {

	/**
	 * 
	 * @param v1
	 *            double value
	 * @param v2
	 *            double value to compare to
	 * @param name
	 *            if a name is given, debug log output is printed in case the
	 *            values are not equal
	 * @return true if both values are equal
	 */
	public static boolean equals(double v1, double v2, String name) {
		if (v1 == v2 || (Double.isNaN(v1) && Double.isNaN(v2))) {
			return true;
		}
		if (name != null) {
			Log.warn(name + " - values differ: " + v1 + " != " + v2);
		}
		return false;
	}
}
