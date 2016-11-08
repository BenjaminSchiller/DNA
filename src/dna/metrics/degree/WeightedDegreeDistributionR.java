package dna.metrics.degree;

import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distr.BinnedIntDistr;

public class WeightedDegreeDistributionR extends WeightedDegreeDistribution
		implements IRecomputation {

	protected BinnedIntDistr inOutDegree;

	public WeightedDegreeDistributionR() {
		super("WeightedDegreeDistributionR");
	}

	public WeightedDegreeDistributionR(String[] nodeTypes) {
		super("WeightedDegreeDistributionR", nodeTypes);
	}

	public WeightedDegreeDistributionR(double binsize) {
		super("WeightedDegreeDistributionR-" + (int) Math.ceil(binsize),
				(int) Math.ceil(binsize));
	}

	public WeightedDegreeDistributionR(String[] nodeTypes, double binsize) {
		super("WeightedDegreeDistributionR-" + binsize, nodeTypes, (int) Math
				.ceil(binsize));
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
