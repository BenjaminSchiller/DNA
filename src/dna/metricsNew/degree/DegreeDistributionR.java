package dna.metricsNew.degree;

import dna.metricsNew.IMetricNew;
import dna.metricsNew.IMetricNew.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

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
