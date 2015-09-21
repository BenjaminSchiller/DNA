package dna.metrics.parallelization.partitioning.schemes;

import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;

public class SizePartitioning extends PartitioningScheme {

	protected int partitionSize;

	public SizePartitioning(PartitioningType partitioningType, int partitionSize) {
		super("SizePartitioning", partitioningType, new IntParameter(
				"partitionSize", partitionSize));
		this.partitionSize = partitionSize;
	}

	@Override
	public List<List<Node>> getPartitioning(Graph g) {
		List<List<Node>> partitioning = this.createNewPartitioning();
		List<Node> current = this.addNewPartition(partitioning);

		for (int i = 0; i <= g.getMaxNodeIndex(); i++) {
			if (current.size() >= this.partitionSize) {
				current = this.addNewPartition(partitioning);
			}
			current.add(g.getNode(i));
		}
		return partitioning;
	}

}
