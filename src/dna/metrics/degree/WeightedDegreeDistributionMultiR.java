package dna.metrics.degree;

import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distr.BinnedIntDistr;

public class WeightedDegreeDistributionMultiR extends
		WeightedDegreeDistributionMulti implements IRecomputation {

	protected BinnedIntDistr inOutDegree;

	protected int index;
	protected String name;

	public WeightedDegreeDistributionMultiR(int index) {
		this("WeightedDegreeDistributionR-", new String[0], index, 1.0);
	}

	public WeightedDegreeDistributionMultiR(int index, double binsize) {
		this("WeightedDegreeDistributionR-", new String[0], index, binsize);
	}

	public WeightedDegreeDistributionMultiR(String[] nodeTypes, int index,
			double binsize) {
		this("WeightedDegreeDistributionR-", nodeTypes, index, binsize);
	}

	public WeightedDegreeDistributionMultiR(String name, String[] nodeTypes,
			int index, double binsize) {
		super(name + index + "-" + binsize, index, nodeTypes, (int) Math
				.ceil(binsize));
		this.name = name;
		this.index = index;
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
