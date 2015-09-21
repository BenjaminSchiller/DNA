package dna.metrics.parallelization.partitioning.nodeAssignment;

import dna.metrics.parallelization.partitioning.Partition;
import dna.updates.update.NodeAddition;
import dna.util.Rand;

public class RandomNodeAssignment extends NodeAssignment {

	public RandomNodeAssignment() {
		super("RandomNodeAssignment");
	}

	@Override
	public Partition assignNode(Partition[] partitions, NodeAddition na) {
		return partitions[Rand.rand.nextInt(partitions.length)];
	}

}
