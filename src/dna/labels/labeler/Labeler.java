package dna.labels.labeler;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.labels.Label;
import dna.metrics.IMetric;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

/**
 * Labeller are used to compute labels.
 * 
 * @author Rwilmes
 * 
 */
public abstract class Labeler {

	public static enum MetricRequirement {
		ALL, ATLEAST_ONE
	}

	private String name;

	public Labeler(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/** Returns if a labeller is applicable/computable given the metrics. **/
	public abstract boolean isApplicable(GraphGenerator gg, BatchGenerator bg,
			IMetric[] metrics);

	public abstract ArrayList<Label> computeLabels(Graph g, Batch batch,
			BatchData batchData, IMetric[] metrics);
}
