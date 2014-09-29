package dna.metricsNew.motifs;

import dna.metricsNew.IMetricNew;
import dna.metricsNew.IMetricNew.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class DirectedMotifsR extends DirectedMotifs implements
		IRecomputation {

	public DirectedMotifsR() {
		super("DirectedMotifsR", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
