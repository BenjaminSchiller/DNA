package dna.parallel.partitioning;

import java.util.ArrayList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.parallel.partition.AllPartitions;
import dna.parallel.partition.NodeCutPartition;
import dna.parallel.partition.SeparatedPartition;
import dna.parallel.partition.OverlappingPartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class Partitioning extends ParameterList {

	public Partitioning(String name, Parameter... parameters) {
		super(name, parameters);
	}

	protected abstract List<Node>[] partition(Graph g, int partitionCount);

	@SuppressWarnings("rawtypes")
	public AllPartitions getPartition(PartitionType partitionType, Graph g,
			int partitionCount) {
		switch (partitionType) {
		case NodeCut:
			return NodeCutPartition
					.partition(this.getName(), PartitionType.NodeCut, g,
							this.partition(g, partitionCount));
		case SEPARATED:
			return SeparatedPartition.partition(this.getName(),
					PartitionType.SEPARATED, g,
					this.partition(g, partitionCount));
		case OVERLAPPING:
			return OverlappingPartition.partition(this.getName(),
					PartitionType.OVERLAPPING, g,
					this.partition(g, partitionCount));
		default:
			throw new IllegalArgumentException("unknown partition type: "
					+ partitionType);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<Node>[] getInitialList(Graph g, int partitionCount) {
		List<Node>[] p = (List<Node>[]) new ArrayList[partitionCount];
		for (int i = 0; i < p.length; i++) {
			p[i] = new ArrayList<Node>(1 + g.getNodeCount() / partitionCount);
		}
		return p;
	}

	protected List<Node>[] split(Graph g, List<Node> sorted, int partitionCount) {
		int partitionSize = (int) Math.ceil(1.0 * g.getNodeCount()
				/ partitionCount);
		List<Node>[] partitioning = getInitialList(g, partitionCount);

		int index = 0;
		for (Node n : sorted) {
			if (partitioning[index].size() >= partitionSize) {
				index++;
			}
			partitioning[index].add(n);
		}

		return partitioning;
	}
}
