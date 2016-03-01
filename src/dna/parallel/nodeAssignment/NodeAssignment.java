package dna.parallel.nodeAssignment;

import dna.parallel.partition.AllPartitions;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;

public abstract class NodeAssignment {

	protected int partitionCount;

	public NodeAssignment(int partitionCount) {
		this.partitionCount = partitionCount;
	}

	@SuppressWarnings("rawtypes")
	public abstract int assignNode(AllPartitions all, Batch b, NodeAddition na);
}
