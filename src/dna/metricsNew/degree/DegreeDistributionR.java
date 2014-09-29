package dna.metricsNew.degree;

import dna.metrics.Metric.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class DegreeDistributionR extends DegreeDistribution implements
		IRecomputation {

	public DegreeDistributionR() {
		super("DegreeDistributionR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
