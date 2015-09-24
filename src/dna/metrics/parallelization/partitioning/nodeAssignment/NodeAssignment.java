package dna.metrics.parallelization.partitioning.nodeAssignment;

import dna.metrics.parallelization.partitioning.Partition;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class NodeAssignment extends ParameterList {

	public NodeAssignment(String name, Parameter... parameters) {
		super(name, parameters);
	}

	public abstract Partition assignNode(PartitioningScheme scheme,
			NodeAddition na, Batch b);
}
