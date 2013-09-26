package dna.util;

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
}
