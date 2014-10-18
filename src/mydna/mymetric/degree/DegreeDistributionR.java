package mydna.mymetric.degree;

import dna.metrics.algorithms.IRecomputation;

public class DegreeDistributionR extends DegreeDistribution implements
		IRecomputation {

	public DegreeDistributionR() {
		super("DegreeDistributionR");
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
