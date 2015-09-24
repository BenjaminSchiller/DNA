package dna.metrics.parallelization.partitioning.nodeAssignment;

import dna.metrics.parallelization.partitioning.Partition;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.util.Rand;

public class RandomNodeAssignment extends NodeAssignment {

	public RandomNodeAssignment() {
		super("RandomNodeAssignment");
	}

	@Override
	public Partition assignNode(PartitioningScheme scheme, NodeAddition na,
			Batch b) {
		return scheme.partitions[Rand.rand.nextInt(scheme.partitions.length)];
	}

}
