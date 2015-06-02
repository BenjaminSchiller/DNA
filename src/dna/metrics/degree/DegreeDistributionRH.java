package dna.metrics.degree;

import dna.metrics.algorithms.IRecomputation;

public class DegreeDistributionRH extends DegreeDistribution implements
		IRecomputation {

	public DegreeDistributionRH() {
		super("DegreeDistributionRH", MetricType.heuristic);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
