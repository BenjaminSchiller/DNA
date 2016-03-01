package dna.parallel.nodeAssignment;

import dna.parallel.partition.AllPartitions;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.util.Rand;

public class RandomNodeAssignment extends NodeAssignment {

	public RandomNodeAssignment(int partitionCount) {
		super(partitionCount);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int assignNode(AllPartitions all, Batch b, NodeAddition na) {
		return Rand.rand.nextInt(this.partitionCount);
	}

}
