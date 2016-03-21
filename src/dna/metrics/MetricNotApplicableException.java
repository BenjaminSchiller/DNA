package dna.metrics;

import dna.graph.IGraph;
import dna.updates.batch.Batch;

public class MetricNotApplicableException extends Exception {

	private static final long serialVersionUID = 7635116747945152349L;

	public MetricNotApplicableException(IMetric m, IGraph g) {
		super("metric " + m.getName() + " is not applicable to graph "
				+ g.getName());
	}

	public MetricNotApplicableException(IMetric m, Batch b) {
		super("metric " + m.getName() + " is not applicable to batch "
				+ b.toString());
	}

}
