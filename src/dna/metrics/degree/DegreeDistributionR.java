package dna.metrics.degree;

import dna.metrics.IMetric;
import dna.metrics.algorithms.IRecomputation;

public class DegreeDistributionR extends DegreeDistribution implements
		IRecomputation {

	public DegreeDistributionR() {
		super("DegreeDistributionR", IMetric.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
