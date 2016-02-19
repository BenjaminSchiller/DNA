package dna.labels.labeler;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.labels.Label;
import dna.metrics.IMetric;
import dna.metrics.degree.DegreeDistributionR;
import dna.metrics.degree.DegreeDistributionU;
import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.Log;

/**
 * Simple implementation of a labeller. Checks the max-degree in the graph and
 * therefore decides what value it gives.
 * 
 * @author Rwilmes
 * 
 */
public class EasyDegreeLabeler extends Labeler {

	protected static String name = "easy-degree-labeller";

	public EasyDegreeLabeler() {
		super(name);
	}

	@Override
	public ArrayList<Label> computeLabels(Graph g, Batch batch,
			BatchData batchData, IMetric[] metrics) {
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
	public boolean isApplicable(GraphGenerator gg, BatchGenerator bg,
			IMetric[] metrics) {
		IMetric degreeMetric = Labeler.getMetric(metrics,
				DegreeDistributionR.class, DegreeDistributionU.class);
		if (degreeMetric == null) {
			Log.warn(this.name + ":  o DegreeDistribution found!");
			return false;
		}
		return true;
	}

}
