package dna.parallel.auxData;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.parallel.partition.Partition;
import dna.parallel.partition.Partition.PartitionType;

/**
 * 
 * This class holds the auxiliary data which is produced during partitioning.
 * This abstract version holds a list of all nots assigned to each partition.
 * Addition data that is required for the collation is added by the respective
 * sub-classes of AuxData.
 * 
 * @author benni
 *
 * @param <T>
 *            partition type
 */
public abstract class AuxData<T extends Partition> {

	public static enum AuxWriteType {
		Add, Remove, Init
	}

	public PartitionType partitionType;
	public GraphDataStructure gds;
	public Set<Node>[] nodesOfPartitions;
	public HashMap<Node, Integer> mapping;

	public AuxData(PartitionType partitionType, GraphDataStructure gds,
			Set<Node>[] nodesOfPartitions) {
		this.partitionType = partitionType;
		this.gds = gds;
		this.nodesOfPartitions = nodesOfPartitions;
		this.mapping = new HashMap<Node, Integer>();
		for (int i = 0; i < nodesOfPartitions.length; i++) {
			for (Node n : nodesOfPartitions[i]) {
				this.mapping.put(n, i);
			}
		}
	}

	public AuxData(PartitionType partitionType, GraphDataStructure gds,
			int partitionCount) {
		this(partitionType, gds, getInitialNodes(partitionCount));
	}

	@SuppressWarnings("unchecked")
	public static Set<Node>[] getInitialNodes(int partitionCount) {
		Set<Node>[] nodes = new Set[partitionCount];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new HashSet<Node>();
		}
		return nodes;
	}

	public int getPartitionCount() {
		return this.nodesOfPartitions.length;
	}

	public int getNodeCount() {
		int sum = 0;
		for (Set<Node> s : this.nodesOfPartitions) {
			sum += s.size();
		}
		return sum;
	}

	public String getSuffix(AuxWriteType auxWriteType) {
		return getSuffix(this.partitionType, auxWriteType);
	}

	public static String getSuffix(PartitionType partitionType,
			AuxWriteType auxWriteType) {
		return ".aux." + partitionType + "." + auxWriteType;
	}

	@SuppressWarnings("rawtypes")
	public static AuxData read(GraphDataStructure gds, int partitionCount,
			String dir, String filename) throws IOException {
		String[] temp = filename.split("\\.");
		PartitionType partitionType = PartitionType
				.valueOf(temp[temp.length - 2]);
		switch (partitionType) {
		case NodeCut:
			return NodeCutAuxData.read(gds, partitionCount, dir, filename);
		case SEPARATED:
			return SeparatedAuxData.read(gds, partitionCount, dir,
					filename);
		case OVERLAPPING:
			return OverlappingAuxData.read(gds, partitionCount, dir, filename);
		default:
			throw new IllegalArgumentException("unknown partition type: "
					+ partitionType);
		}
	}

	public abstract void write(String dir, String filename);

	public abstract void add(AuxData<T> add);

	public abstract void remove(AuxData<T> remove);

	public static final String sep0 = ";";
	public static final String sep1 = " ";
	public static final String sep2 = ",";

	protected String getNodesString(Set<Node> nodes) {
		StringBuffer buff = new StringBuffer();
		for (Node n : nodes) {
			buff.append(sep1 + n.getIndex());
		}
		return buff.toString();
	}

	protected String getEdgesString(Set<Edge> edges) {
		StringBuffer buff = new StringBuffer();
		for (Edge e : edges) {
			buff.append(sep1 + e.getN1Index() + sep2 + e.getN2Index());
		}
		return buff.toString();
	}

	protected Set<Node> getNodes(String str) {
		Set<Node> nodes = new HashSet<Node>();
		if (str.trim().length() == 0) {
			return nodes;
		}
		String[] elements = str.trim().split(sep1);
		for (String n : elements) {
			nodes.add(gds.newNodeInstance(Integer.parseInt(n)));
		}
		return nodes;
	}

	protected Set<Edge> getEdges(String str) {
		Set<Edge> edges = new HashSet<Edge>();
		if (str.trim().length() == 0) {
			return edges;
		}
		String[] elements = str.trim().split(sep1);
		for (String e : elements) {
			String[] indexes = e.split(sep2);
			edges.add(gds.newEdgeInstance(
					gds.newNodeInstance(Integer.parseInt(indexes[0])),
					gds.newNodeInstance(Integer.parseInt(indexes[1]))));
		}
		return edges;
	}

	public int getPartitionIndex(Node n) {
		return this.mapping.get(n);
	}
}
