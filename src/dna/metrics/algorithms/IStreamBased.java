package dna.metrics.algorithms;

import dna.metrics.IMetric;

public interface IStreamBased extends IMetric {
	public boolean init();

	public boolean query();
}
