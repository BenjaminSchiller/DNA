package dna.metrics.parallelization.partitioning.schemes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.util.Rand;

public class RandomPartitioning extends PartitioningScheme {

	public RandomPartitioning(PartitioningType partitioningType,
			int partitionCount) {
		super("RandomPartitioning", partitioningType, partitionCount);
	}

	@Override
	public List<List<Node>> getPartitioning(Graph g) {
		ArrayList<List<Node>> partitioning = new ArrayList<List<Node>>(
				this.partitionCount);

		for (int i = 0; i < this.partitionCount; i++) {
			partitioning.add(new LinkedList<Node>());
		}

		for (IElement n_ : g.getNodes()) {
			partitioning.get(Rand.rand.nextInt(this.partitionCount)).add(
					(Node) n_);
		}

		return partitioning;
	}

}
