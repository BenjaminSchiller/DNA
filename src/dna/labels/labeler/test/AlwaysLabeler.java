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
 * A labeler who always puts his label. <br>
 * <br>
 * 
 * Primarily used for testing purposes.
 * 
 * @author Rwilmes
 * 
 */
public class AlwaysLabeler extends Labeler {

	private static String name = "AlwaysLabeler";

	private static Label alwaysLabel = new Label("always", "label", "1");

	public AlwaysLabeler() {
		super(name);
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
		list.add(alwaysLabel);
		return list;
	}

}
