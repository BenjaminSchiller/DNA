package dna.metrics.motifs;

import dna.metrics.IMetricNew;
import dna.metrics.algorithms.IRecomputation;

public class DirectedMotifsR extends DirectedMotifs implements IRecomputation {

	public DirectedMotifsR() {
		super("DirectedMotifsR", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
