package dna.metrics.parallelization.partitioning.schemes;

import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;

public class EqualSizePartitioning extends PartitioningScheme {

	public EqualSizePartitioning(PartitioningType partitioningType,
			int partitionCount) {
		super("EqualSizePartitioning", partitioningType, partitionCount);
	}

	@Override
	public List<List<Node>> getPartitioning(Graph g) {
		int partitionSize = (int) Math.ceil((double) g.getNodeCount()
				/ (double) this.partitionCount);
		List<List<Node>> partitioning = this.createNewPartitioning();
		List<Node> current = this.addNewPartition(partitioning);

		for (int i = 0; i <= g.getMaxNodeIndex(); i++) {
			if (current.size() >= partitionSize) {
				current = this.addNewPartition(partitioning);
			}
			current.add(g.getNode(i));
		}
		return partitioning;
	}

}
