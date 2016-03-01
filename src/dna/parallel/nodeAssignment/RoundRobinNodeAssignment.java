package dna.parallel.nodeAssignment;

import dna.parallel.partition.AllPartitions;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;

public class RoundRobinNodeAssignment extends NodeAssignment {

	protected int counter = 0;

	public RoundRobinNodeAssignment(int partitionCount) {
		super(partitionCount);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int assignNode(AllPartitions all, Batch b, NodeAddition na) {
		this.counter = (this.counter + 1) % this.partitionCount;
		return this.counter;
	}

}
