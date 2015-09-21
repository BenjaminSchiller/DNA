package dna.metrics.parallelization.collation;

import dna.metrics.parallelization.partitioning.Partition;

public interface PartitionedMetric {
	public Partition getPartition();

	public void setPartition(Partition partition);
}
