package dna.metrics.parallelization.partitioning.nodeAssignment;

import dna.metrics.parallelization.partitioning.Partition;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;

public class RoundRobinNodeAssignment extends NodeAssignment {

	protected int index = -1;

	public RoundRobinNodeAssignment() {
		super("RoundRobinNodeAssignment");
	}

	@Override
	public Partition assignNode(PartitioningScheme scheme, NodeAddition na,
			Batch b) {
		this.index = (this.index + 1) % scheme.partitions.length;
		return scheme.partitions[this.index];
	}

}
