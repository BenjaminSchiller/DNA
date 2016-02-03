package dna.util.fromArgs;

import dna.metrics.parallelization.partitioning.nodeAssignment.FirstEdgeNodeAssignment;
import dna.metrics.parallelization.partitioning.nodeAssignment.NodeAssignment;
import dna.metrics.parallelization.partitioning.nodeAssignment.RandomNodeAssignment;
import dna.metrics.parallelization.partitioning.nodeAssignment.RoundRobinNodeAssignment;

public class NodeAssignmentFromArgs {
	public static enum NodeAssignmentType {
		FIRST_EDGE, RANDOM, ROUND_ROBIN
	}

	public static NodeAssignment parse(NodeAssignmentType nodeAssignmentType,
			String... args) {
		switch (nodeAssignmentType) {
		case FIRST_EDGE:
			return new FirstEdgeNodeAssignment();
		case RANDOM:
			return new RandomNodeAssignment();
		case ROUND_ROBIN:
			return new RoundRobinNodeAssignment();
		default:
			throw new IllegalArgumentException("unknown node assignment type: "
					+ nodeAssignmentType);
		}
	}
}
