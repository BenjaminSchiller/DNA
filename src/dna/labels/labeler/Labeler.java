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
 * Labeler are used to compute labels.
 * 
 * @author Rwilmes
 * 
 */
public abstract class Labeler {
	
	private String name;

	public Labeler(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Returns if a labeller is applicable/computable given the GraphGenerator,
	 * BatchGenerator and metrics.<br>
	 * <br>
	 * 
	 * <b>Note:</b> Each Labeler shall add some kind of log-warning that is
	 * printed if it is not applicable and why.
	 **/
	public abstract boolean isApplicable(GraphGenerator gg, BatchGenerator bg,
			IMetric[] metrics);

	/** Computes and returns a list of labels. **/
	public abstract ArrayList<Label> computeLabels(Graph g, Batch batch,
			BatchData batchData, IMetric[] metrics);

	public static IMetric getMetric(IMetric[] metrics,
			Class<? extends IMetric> mo) {
		for (IMetric m : metrics) {
			if (m.equals(mo))
				return m;
		}
		return null;
	}

	/**
	 * Returns the first metric, which is a subclass of the given class(es).
	 * Returns null if no matching metric is present.
	 **/
	@SafeVarargs
	public static IMetric getMetric(IMetric[] metrics,
			Class<? extends IMetric>... classes) {
		for (IMetric m : metrics) {
			for (Class<? extends IMetric> mClass : classes) {
				if (m.getClass().equals(mClass)) {
					return m;
				}
			}
		}
		return null;
	}
}
