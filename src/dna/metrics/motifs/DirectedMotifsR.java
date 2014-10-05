package dna.metrics.motifs;

import dna.metrics.IMetric;
import dna.metrics.algorithms.IRecomputation;

public class DirectedMotifsR extends DirectedMotifs implements IRecomputation {

	public DirectedMotifsR() {
		super("DirectedMotifsR", IMetric.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
