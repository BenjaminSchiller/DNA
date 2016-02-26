package dna.labels.labeler.test;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.labels.Label;
import dna.labels.labeler.Labeler;
import dna.metrics.IMetric;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

/**
 * A labeler who puts his label on to every second batch. <br>
 * <br>
 * 
 * Primarily used for testing purposes.
 * 
 * @author Rwilmes
 * 
 */
public class AlternatingLabeler extends Labeler {

	private static String name = "AlternatingLabeler";
	private static Label alternatingLabel = new Label(name, "type", "true");

	public boolean flag;

	public AlternatingLabeler() {
		super(name);
		flag = true;
	}

	@Override
	public boolean isApplicable(GraphGenerator gg, BatchGenerator bg,
			IMetric[] metrics) {
		return true;
	}

	@Override
	public ArrayList<Label> computeLabels(Graph g, Batch batch,
			BatchData batchData, IMetric[] metrics) {
		ArrayList<Label> list = new ArrayList<Label>();
		if (flag)
			list.add(alternatingLabel);

		flag = !flag;
		return list;
	}

}
