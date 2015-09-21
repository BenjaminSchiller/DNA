package dna.metrics.parallelization.partitioning.nodeAssignment;

import dna.metrics.parallelization.partitioning.Partition;
import dna.updates.update.NodeAddition;

public class RoundRobinNodeAssignment extends NodeAssignment {

	protected int index = -1;

	public RoundRobinNodeAssignment() {
		super("RoundRobinNodeAssignment");
	}

	@Override
	public Partition assignNode(Partition[] partitions, NodeAddition na) {
		this.index = (this.index + 1) % partitions.length;
		return partitions[this.index];
	}

}
