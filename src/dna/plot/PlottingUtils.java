package dna.plot;

import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedMetric;
import dna.series.data.BatchData;
import dna.series.data.MetricData;

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

	/**
	 * Checks if the given value of the given domain is contained in atleast on
	 * of the given batches. If yes, true is returned.
	 * 
	 * @param domain
	 *            Domain to be checked.
	 * @param value
	 *            Value to be checked.
	 * @param batches
	 *            Array of batches to be checked.
	 * @return True if domain and value is found in atleast one of the batches.
	 */
	public static boolean isContained(String domain, String value,
			BatchData[] batches) {
		boolean contained = false;

		// if domain or value null, return false
		if (domain == null || value == null)
			return false;

		// check if statistic
		if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
			for (BatchData b : batches) {
				if (b.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if general runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
			for (BatchData b : batches) {
				if (b.getGeneralRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if metric runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
			for (BatchData b : batches) {
				if (b.getMetricRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if domain is a metric
		for (BatchData b : batches) {
			if (b.getMetrics().getNames().contains(domain)) {
				MetricData m = b.getMetrics().get(domain);

				// if value
				if (m.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if distribution
				if (m.getDistributions().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if nodevaluelist
				if (m.getNodeValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}

		return contained;
	}

	/**
	 * Checks if the given value of the given domain is contained in atleast on
	 * of the given batches. If yes, true is returned.
	 * 
	 * @param domain
	 *            Domain to be checked.
	 * @param value
	 *            Value to be checked.
	 * @param batches
	 *            Array of batches to be checked.
	 * @return True if domain and value is found in atleast one of the batches.
	 */
	public static boolean isContained(String domain, String value,
			AggregatedBatch[] batches) {
		boolean contained = false;

		// if domain or value null, return false
		if (domain == null || value == null)
			return false;

		// check if statistic
		if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
			for (AggregatedBatch b : batches) {
				if (b.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if general runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
			for (AggregatedBatch b : batches) {
				if (b.getGeneralRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if metric runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
			for (AggregatedBatch b : batches) {
				if (b.getMetricRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if domain is a metric
		for (AggregatedBatch b : batches) {
			if (b.getMetrics().getNames().contains(domain)) {
				AggregatedMetric m = b.getMetrics().get(domain);

				// if value
				if (m.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if distribution
				if (m.getDistributions().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if nodevaluelist
				if (m.getNodeValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}

		return contained;
	}

	/** Checks if the value and domain are contained in the given batch. **/
	public static boolean isContained(String domain, String value,
			AggregatedBatch batch) {
		return PlottingUtils.isContained(domain, value,
				new AggregatedBatch[] { batch });
	}

	/** Checks if the value and domain are contained in the given batch. **/
	public static boolean isContained(String domain, String value,
			BatchData batch) {
		return PlottingUtils.isContained(domain, value,
				new BatchData[] { batch });
	}

}
