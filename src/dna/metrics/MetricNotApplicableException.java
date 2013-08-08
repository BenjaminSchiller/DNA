package dna.metrics;

import dna.graph.Graph;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public class MetricNotApplicableException extends Exception {

	private static final long serialVersionUID = 7635116747945152349L;

	public MetricNotApplicableException(Metric m, Graph g) {
		super("metric " + m.getName() + " is not applicable to graph "
				+ g.getName());
	}

	public MetricNotApplicableException(Metric m, Batch b) {
		super("metric " + m.getName() + " is not applicable to batch "
				+ b.toString());
	}

}
