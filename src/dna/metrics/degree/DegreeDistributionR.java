package dna.metrics.degree;

import dna.metrics.IMetricNew;
import dna.metrics.algorithms.IRecomputation;

public class DegreeDistributionR extends DegreeDistribution implements
		IRecomputation {

	public DegreeDistributionR() {
		super("DegreeDistributionR", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
