package dna.plot;

/**
 * Plotting class which holds static utility methods for plotting.
 * 
 * @author Rwilmes
 * @date 05.11.2014
 */
public class PlottingUtils {

	/** Returns the first value inside the expression. **/
	public static String getValueFromExpression(String expr) {
		String[] split = expr.split("\\$");
		for (int i = 0; i < split.length; i++) {
			if (split.length > 1) {
				String[] split2 = split[1]
						.split(PlotConfig.customPlotDomainDelimiter);
				if (split2.length > 1) {
					String value = "";
					for (int j = 1; j < split2.length; j++)
						value += split2[j];
					return value;
				}
				return split[1];
			}
		}
		return null;
	}

	/** Returns the domain of the first value inside the expression. **/
	public static String getDomainFromExpression(String expr,
			String generalDomain) {
		String[] split = expr.split("\\$");
		for (int i = 0; i < split.length; i++) {
			if (split.length > 1) {
				String[] split2 = split[1]
						.split(PlotConfig.customPlotDomainDelimiter);
				if (split2.length > 1) {
					return split2[0];
				} else {
					return generalDomain;
				}
			}
		}
		return null;
	}
}
