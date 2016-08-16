package dna.metrics.algorithms;

import dna.metrics.IMetric;

public interface ISnapshotBased extends IMetric {
	public boolean recompute();
}
