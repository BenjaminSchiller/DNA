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
 * A labeler who labels all batches inside the given interval.
 * 
 * @author Rwilmes
 */
public class IntervalLabeler extends Labeler {

	private static String name = "IntervalLabeler";

	private String type;
	private long from;
	private long to;

	public IntervalLabeler(String type, long from, long to) {
		this(name, type, from, to);
	}

	public IntervalLabeler(String name, String type, long from, long to) {
		super(name);
		this.type = type;
		this.from = from;
		this.to = to;
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

		if (this.from <= batchData.getTimestamp()
				&& batchData.getTimestamp() <= this.to)
			list.add(new Label(name, type, "1"));

		return list;
	}

}
