package dna.parallel.nodeAssignment;

public class NodeAssignmentFromArgs {

	public static enum NodeAssignmentType {
		Random, RoundRobin
	}

	public static NodeAssignment parse(NodeAssignmentType nodeAssignmentType,
			int partitionCount, String... args) {
		switch (nodeAssignmentType) {
		case Random:
			return new RandomNodeAssignment(partitionCount);
		case RoundRobin:
			return new RoundRobinNodeAssignment(partitionCount);
		default:
			throw new IllegalArgumentException("unknown node assignment type: "
					+ nodeAssignmentType);
		}
	}
}
