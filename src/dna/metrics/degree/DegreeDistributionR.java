package dna.metrics.degree;

import dna.metrics.algorithms.IRecomputation;

public class DegreeDistributionR extends DegreeDistribution implements
		IRecomputation {

	public DegreeDistributionR() {
		super("DegreeDistributionR");
	}

	public DegreeDistributionR(String[] nodeTypes) {
		super("DegreeDistributionR", nodeTypes);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
