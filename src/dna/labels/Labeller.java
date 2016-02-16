package dna.labels;

import java.util.ArrayList;

import dna.metrics.IMetric;
import dna.series.data.BatchData;

/**
 * Labeller are used to compute labels.
 * 
 * @author Rwilmes
 * 
 */
public abstract class Labeller {

	public static enum MetricRequirement {
		ALL, ATLEAST_ONE
	}

	private String name;

	public Labeller(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Returns if a labeller is applicable/computable given the metrics.
	 * Depending on the specified labeller-metric-requirement a decision is
	 * made. <br>
	 * 
	 * For this purpose each labeller has to implement its own
	 * getRequiredMetrics-method, which returns an array of metric-names.
	 **/
	public boolean isApplicable(IMetric[] metrics) {
		String[] reqMetrics = getRequiredMetrics();
		MetricRequirement req = getRequirementStrategy();
		boolean[] flags = new boolean[reqMetrics.length];

		// cross-check metrics and set flags
		for (int i = 0; i < reqMetrics.length; i++) {
			String reqMetric = reqMetrics[i];
			for (IMetric m : metrics) {
				if (m.getName().equals(reqMetric))
					flags[i] = true;
			}
		}

		// check flags and decide
		for (boolean flag : flags) {
			if (flag == true && req.equals(MetricRequirement.ATLEAST_ONE))
				return true;
			if (flag == false && req.equals(MetricRequirement.ALL))
				return false;
		}

		switch (req) {
		case ATLEAST_ONE:
			return false;
		case ALL:
			return true;
		default:
			return false;
		}
	}

	public abstract ArrayList<Label> computeLabels(BatchData batchData,
			IMetric[] metrics);

	public abstract String[] getRequiredMetrics();

	public abstract MetricRequirement getRequirementStrategy();
}
