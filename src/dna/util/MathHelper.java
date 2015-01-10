package dna.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MathHelper {
	/**
	 * Parse a String that is known to contain an integer to an integer
	 * 
	 * @param s
	 * @return
	 */
	public static int parseInt(final String s) {
		int result = 0;

		for (int i = 0; i < s.length(); i++) {
			if ((s.charAt(i) - '0' > 9) || (s.charAt(i) - '0' < 0))
				break;
			result = result * 10;
			result += (s.charAt(i) - '0');
		}

		return result;
	}

	/** Trims the String which is known to contain a double. **/
	public static String trim(String s, char c) {
		boolean finished = false;
		int zeros = 0;
		for (int j = s.length() - 1; j >= 0 && !finished; j--) {
			// if last char is not 0, finish
			if (j == s.length() - 1 && s.charAt(j) != '0')
				return s;
			// count zeros
			if (s.charAt(j) == '0') {
				if (j != 0)
					zeros++;
			} else {
				if (s.charAt(j) == ',' || s.charAt(j) == '.')
					zeros++;

				finished = true;
				continue;
			}
		}

		// return substring
		return s.substring(0, s.length() - zeros);
	}

	/** Formats the double in a better readable format. **/
	public static String format(double d) {
		NumberFormat f = NumberFormat.getInstance();
		if (f instanceof DecimalFormat) {
			DecimalFormat decFormat = (DecimalFormat) f;

			String patternTemp = "";
			double x = d;
			if (x < 0)
				x = x * -1;

			if (x < 10000) {
				patternTemp = "0";
			}
			if (x < 1000) {
				patternTemp = "0.0";
			}
			if (x < 100) {
				patternTemp = "0.00";
			}
			if (x < 10) {
				patternTemp = "0.000";
			}
			if (x < 1) {
				patternTemp = "0.00000";
			}
			if (x < 0.1) {
				patternTemp = "0.000000";
			}
			if (x < 0.01) {
				patternTemp = "0.0000000";
			}
			if (x < 0.001) {
				patternTemp = "0.0000000";
			}
			if (x < 0.0001) {
				patternTemp = "0.######E0";
			}
			if (x == 0) {
				patternTemp = "0";
			}

			if (x > 10000000) {
				decFormat.applyPattern("#.00M");
				return MathHelper.trim(decFormat.format(d / 1000000), '0');
			} else if (x > 10000) {
				decFormat.applyPattern("#.00k");
				return MathHelper.trim(decFormat.format(d / 1000), '0');
			}

			// apply pattern
			decFormat.applyPattern(patternTemp);

			// return
			return MathHelper.trim(decFormat.format(d), '0');
		}

		return "" + d;
	}
}
