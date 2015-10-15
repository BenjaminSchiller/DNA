package dna.metrics.parallelization.partitioning.schemes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.parallelization.collation.PartitionedMetric;
import dna.metrics.parallelization.partitioning.NodeCutPartition;
import dna.metrics.parallelization.partitioning.NonOverlappingPartition;
import dna.metrics.parallelization.partitioning.OverlappingPartition;
import dna.metrics.parallelization.partitioning.Partition;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;
import dna.util.parameters.StringParameter;

/**
 * 
 * Abstract class for the partitioning of a graph into a given number of
 * partitions (either overlapping or non-overlapping). Implementations of such a
 * partitioning are required only to create a partitioning of the set of all
 * nodes (a list of node lists) while the generation of overlapping or
 * non-overlapping partition objects is done by components of this super class.
 * 
 * The partitioning is initialized once. Then, this class holds the partitions
 * (as an array) as well as a mapping of nodes to partitions.
 * 
 * @author benni
 *
 */
public abstract class PartitioningScheme extends ParameterList {

	protected PartitioningType partitioningType;
	protected int partitionCount;

	public Partition[] partitions;
	public HashMap<Node, Partition> partitionMap;

	/**
	 * 
	 * For non-overlapping partitions, each partition only stores the subgraph
	 * induced by the set of nodes in the partition. For overlapping partitions,
	 * the neighbors of the partition are also added to the graph as well as all
	 * edges between them and the initial nodes (and between these neighbors).
	 * 
	 * @author benni
	 *
	 */
	public static enum PartitioningType {
		OVERLAPPING, NON_OVERLAPPING, NODE_CUT
	}

	public PartitioningScheme(String name, PartitioningType partitioningType,
			int partitionCount, Parameter... parameters) {
		super(name, new Parameter[] {
				new StringParameter("partitioningType",
						partitioningType.toString()),
				new IntParameter("partitionCount", partitionCount) },
				parameters);
		this.partitioningType = partitioningType;
		this.partitionCount = partitionCount;
	}

	/**
	 * 
	 * Initializes a partitioning of the given graph for the specified metric.
	 * For each partition, the metric is cloned and assigned to the partition.
	 * 
	 * @param g
	 *            graph that should be partitioned
	 * @param m
	 *            metric (a clone is assigned to each partition)
	 */
	public void init(Graph g, Metric m) {
		List<List<Node>> p = this.getPartitioning(g);
		this.partitionMap = new HashMap<Node, Partition>();
		// this.partitions = new Partition[p.size()];
		// int index = 0;
		// for (List<Node> nodes : p) {
		// this.partitions[index] = this.createPartition("p" + index, g,
		// nodes, m);
		// index++;
		// }
		// index = 0;
		// for (List<Node> nodes : p) {
		// if (this.partitions[index].getMetric() instanceof PartitionedMetric)
		// {
		// ((PartitionedMetric) this.partitions[index].getMetric())
		// .setPartition(this.partitions[index]);
		// }
		// for (Node n : nodes) {
		// this.partitionMap.put(n, this.partitions[index]);
		// }
		// index++;
		// }

		switch (partitioningType) {
		case NON_OVERLAPPING:
			this.partitions = NonOverlappingPartition.getPartitions(g, p, m,
					this.partitionMap);
			break;
		case OVERLAPPING:
			this.partitions = OverlappingPartition.getPartitions(g, p, m,
					this.partitionMap);
			break;
		case NODE_CUT:
			this.partitions = NodeCutPartition.getPartitions(g, p, m,
					this.partitionMap);
			break;
		default:
			throw new IllegalArgumentException("unknown partitioning type: "
					+ partitioningType);
		}

		for (Partition partition : this.partitions) {
			if (partition.getMetric() instanceof PartitionedMetric) {
				((PartitionedMetric) partition.getMetric())
						.setPartition(partition);
			}
		}
	}

	// protected Partition createPartition(String name, Graph g, List<Node>
	// nodes,
	// Metric m) {
	// switch (partitioningType) {
	// case NON_OVERLAPPING:
	// return NonOverlappingPartition.getPartition(name, g, nodes, m);
	// case OVERLAPPING:
	// return OverlappingPartition.getPartition(name, g, nodes, m);
	// default:
	// throw new IllegalArgumentException("unknown partitioning type: "
	// + partitioningType);
	// }
	// }

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
