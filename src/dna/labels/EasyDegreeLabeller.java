package dna.labels;

import java.util.ArrayList;

import dna.metrics.IMetric;
import dna.series.data.BatchData;
import dna.series.data.MetricData;

/**
 * Simple implementation of a labeller. Checks the max-degree in the graph and
 * therefore decides what value it gives.
 * 
 * @author Rwilmes
 * 
 */
public class EasyDegreeLabeller extends Labeller {

	protected static String name = "easy-degree-labeller";

	public EasyDegreeLabeller() {
		super(name);
	}

	@Override
	public ArrayList<Label> compute(BatchData batchData, IMetric[] metrics) {
		// init list of labels
		ArrayList<Label> labels = new ArrayList<Label>();

		// get either
		MetricData degreeDist = batchData.getMetrics().get(
				"DegreeDistributionR");
		degreeDist = (degreeDist == null) ? batchData.getMetrics().get(
				"DegreeDistributionU") : degreeDist;

		// if both empty -> return empty label list
		if (degreeDist == null)
			return labels;

		double degMax = degreeDist.getValues().get("DegreeMax").getValue();
		double labelValue = 0;

		if (degMax >= 10)
			labelValue = 0.1;
		if (degMax >= 40)
			labelValue = 0.2;
		if (degMax >= 50)
			labelValue = 0.4;
		if (degMax >= 60)
			labelValue = 0.6;
		if (degMax >= 70)
			labelValue = 0.8;
		if (degMax >= 80)
			labelValue = 1.0;

		labels.add(new Label(this.getName(), "type", "" + labelValue));
		return labels;
	}

	@Override
	public String[] getRequiredMetrics() {
		return new String[] { "DegreeDistributionR", "DegreeDistributionU" };
	}

	@Override
	public MetricRequirement getMetricRequirement() {
		return MetricRequirement.ATLEAST_ONE;
	}

}
