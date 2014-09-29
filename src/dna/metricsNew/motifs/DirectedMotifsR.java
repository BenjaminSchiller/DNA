package dna.metricsNew.motifs;

import dna.metrics.Metric.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class DirectedMotifsR extends DirectedMotifs implements
		IRecomputation {

	public DirectedMotifsR() {
		super("DirectedMotifsR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
