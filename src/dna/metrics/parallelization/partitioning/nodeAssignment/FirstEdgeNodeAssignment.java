package dna.metrics.parallelization.partitioning.nodeAssignment;

import dna.graph.nodes.Node;
import dna.metrics.parallelization.partitioning.Partition;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.util.Rand;

public class FirstEdgeNodeAssignment extends NodeAssignment {

	public FirstEdgeNodeAssignment() {
		super("FirstEdgeNodeAssignment");
	}

	@Override
	public Partition assignNode(PartitioningScheme scheme, NodeAddition na,
			Batch b) {
		for (EdgeAddition ea : b.getEdgeAdditions()) {
			if (ea.getEdge().getN1().equals(na.getNode())
					|| ea.getEdge().getN2().equals(na.getNode())) {
				Partition p = scheme.partitionMap.get(ea.getEdge()
						.getDifferingNode((Node) na.getNode()));
				if (p != null) {
					return p;
				}
			}
		}
		return scheme.partitions[Rand.rand.nextInt(scheme.partitions.length)];
	}
}
