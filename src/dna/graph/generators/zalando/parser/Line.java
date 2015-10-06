package dna.graph.generators.zalando.parser;

public class Line {

	/**
	 * Converts the given boolean value to the string representation used in
	 * Zalando logs and products.csv.
	 * 
	 * @param b
	 *            {@code true} or {@code false}
	 * @return "1" if and only if b is true, else "0".
	 * 
	 * @see #stringToBool(String) The reverse operation
	 */
	public static String boolToString(boolean b) {
		return b ? "1" : "0";
	}

	/**
	 * Takes any string of type [CHARACTER][MINUS][NUMBER], e.g. "p-6" and
	 * returns the number ("6" for the given example).
	 * 
	 * @param column
	 *            A string of type [CHARACTER][MINUS][NUMBER]
	 * @return The number in the given string.
	 */
	public static int getNumber1(String column) {
		return Integer.valueOf(column.replaceAll("\\w-", ""));
	}

	/**
	 * Takes any string of type [CHARACTER][MINUS][NUMBER][UNDERSCORE][NUMBER],
	 * e.g. "p-6_2" and returns the second number ("2" for the give example).
	 * 
	 * @param column
	 *            A string of type
	 *            [CHARACTER][MINUS][NUMBER][UNDERSCORE][NUMBER]
	 * @return The second number in the given string.
	 */
	public static int getNumber2(String column) {
		return Integer.valueOf(column.replaceAll("\\w-\\d*_", ""));
	}

	/**
	 * Takes any string of type [CHARACTER][MINUS][STRING], e.g. "p-6_2" and
	 * returns the string "6_2".
	 * 
	 * @param column
	 *            A string of type [CHARACTER][MINUS][STRING]
	 * @return The second string in the given string.
	 */
	public static String getString2(String column) {
		return column.replaceAll("\\w-", "");
	}

	/**
	 * Converts the given string used in Zalando logs and products.csv to a
	 * boolean value.
	 * 
	 * @param s
	 *            "1" or "0"
	 * @return {@code true} iff s is "1" and {@code false} iff s is "0"
	 * 
	 * @throws IllegalArgumentException
	 *             if s is neither "1" nor "0".
	 * 
	 * @see #boolToString(boolean) The reverse operation
	 */
	public static boolean stringToBool(String s) {
		if (s.equals("1")) {
			return true;
		} else if (s.equals("0")) {
			return false;
		} else {
			throw new IllegalArgumentException(
					"String must be either '1' or '0' to convert it to a boolean expression but given string was something else.");
		}
	}
}
