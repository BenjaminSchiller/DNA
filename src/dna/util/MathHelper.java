package dna.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import dna.util.expr.Expr;
import dna.util.expr.Parser;
import dna.util.expr.SyntaxException;
import dna.util.expr.Variable;

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
		NumberFormat f = NumberFormat.getInstance(Config
				.getLocale("LATEX_DATA_FORMAT_LOCALE"));
		if (f instanceof DecimalFormat) {
			DecimalFormat decFormat = (DecimalFormat) f;

			String patternTemp = "";
			double x = d;
			if (x < 0)
				x = x * -1;

			if (x < 10000) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW10K");
			}
			if (x < 1000) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW1K");
			}
			if (x < 100) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW100");
			}
			if (x < 10) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW10");
			}
			if (x < 1) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW1");
			}
			if (x < 0.1) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW01");
			}
			if (x < 0.01) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW001");
			}
			if (x < 0.001) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW0001");
			}
			if (x < 0.0001) {
				patternTemp = Config.get("DATA_FORMATTING_BELOW0001");
			}
			if (x == 0) {
				patternTemp = Config.get("DATA_FORMATTING_EQUAL0");
			}

			if (x > 10000000) {
				decFormat.applyPattern(Config.get("DATA_FORMATTING_ABOVE10M"));
				return MathHelper.trim(decFormat.format(d / 1000000), '0');
			} else if (x > 10000) {
				decFormat.applyPattern(Config.get("DATA_FORMATTING_ABOVE10K"));
				return MathHelper.trim(decFormat.format(d / 1000), '0');
			}

			// apply pattern
			decFormat.applyPattern(patternTemp);

			// return
			return MathHelper.trim(decFormat.format(d), '0');
		}

		return "" + d;
	}
	
	/** Scales the timestamp according to the expression. **/
	public static long scaleTimestamp(long timestamp, String expression, String variable) {
		// parse expression
		Expr expr = null;
		try {
			expr = Parser.parse(expression);
		} catch (SyntaxException e) {
			// print what went wrong
			if (Config.getBoolean("CUSTOM_PLOT_EXPLAIN_EXPRESSION_FAILURE"))
				System.out.println(e.explain());
			else
				e.printStackTrace();
		}

		// define variable
		Variable v = Variable.make(variable);
		v.setValue(timestamp);

		// return
		return (long) expr.value();
	}
}
