package dna.metrics.parallelization.partitioning.schemes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.parallelization.collation.PartitionedMetric;
import dna.metrics.parallelization.partitioning.NonOverlappingPartition;
import dna.metrics.parallelization.partitioning.OverlappingPartition;
import dna.metrics.parallelization.partitioning.Partition;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;
import dna.util.parameters.StringParameter;

public abstract class PartitioningScheme extends ParameterList {

	protected PartitioningType partitioningType;

	protected Partition[] partitions;
	protected HashMap<Node, Partition> partitionMap;

	public static enum PartitioningType {
		OVERLAPPING, NON_OVERLAPPING
	}

	public PartitioningScheme(String name, PartitioningType partitioningType,
			Parameter... parameters) {
		super(name, new Parameter[] { new StringParameter("partitioningType",
				partitioningType.toString()) }, parameters);
		this.partitioningType = partitioningType;
	}

	public void init(Graph g, Metric m) {
		List<List<Node>> p = this.getPartitioning(g);
		this.partitions = new Partition[p.size()];
		this.partitionMap = new HashMap<Node, Partition>();
		int index = 0;
		for (List<Node> nodes : p) {
			this.partitions[index] = this
					.getPartition("p" + index, g, nodes, m);
			if (this.partitions[index].getMetric() instanceof PartitionedMetric) {
				((PartitionedMetric) this.partitions[index].getMetric())
						.setPartition(this.partitions[index]);
			}
			for (Node n : nodes) {
				this.partitionMap.put(n, this.partitions[index]);
			}
			index++;
		}
	}

	protected Partition getPartition(String name, Graph g, List<Node> nodes,
			Metric m) {
		switch (partitioningType) {
		case NON_OVERLAPPING:
			return NonOverlappingPartition.getPartition(name, g, nodes, m);
		case OVERLAPPING:
			return OverlappingPartition.getPartition(name, g, nodes, m);
		default:
			throw new IllegalArgumentException("unknown partitioning type: "
					+ partitioningType);
		}
	}

	public abstract List<List<Node>> getPartitioning(Graph g);

	public PartitioningType getPartitioningType() {
		return this.partitioningType;
	}

	public Partition[] getPartitions() {
		return this.partitions;
	}

	HashMap<Integer, Node> addedNodes = new HashMap<Integer, Node>();

	protected List<List<Node>> createNewPartitioning() {
		return new ArrayList<List<Node>>();
	}

	protected List<Node> addNewPartition(List<List<Node>> partitioning) {
		ArrayList<Node> current = new ArrayList<Node>();
		partitioning.add(current);
		return current;
	}
}
