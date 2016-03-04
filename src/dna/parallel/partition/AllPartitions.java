package dna.parallel.partition;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.parallel.auxData.AuxData;
import dna.parallel.partition.Partition.PartitionType;
import dna.util.parameters.ParameterList;

public class AllPartitions<T extends Partition, D extends AuxData<T>> extends
		ParameterList {
	public PartitionType partitionType;
	public Graph g;
	public T[] partitions;
	public D auxData;

	// public HashMap<Node, Integer> mapping;

	public AllPartitions(String name, PartitionType partitionType, Graph g,
			T[] partitions, D auxData, HashMap<Node, Integer> mapping) {
		super(name);
		this.partitionType = partitionType;
		this.g = g;
		this.partitions = partitions;
		this.auxData = auxData;
		// this.mapping = mapping;
	}

	public T getPartition(int index) {
		return this.partitions[index];
	}

	public int getPartitionCount() {
		return this.partitions.length;
	}

	public int getNodeSum() {
		int sum = 0;
		for (T p : partitions) {
			sum += p.g.getNodeCount();
		}
		return sum;
	}

	public int getEdgeSum() {
		int sum = 0;
		for (T p : partitions) {
			sum += p.g.getEdgeCount();
		}
		return sum;
	}

	public String toString() {
		return partitionType + " (" + g.getNodeCount() + "/" + g.getEdgeCount()
				+ ") with " + partitions.length + " partitions ("
				+ this.getNodeSum() + "/" + this.getEdgeSum() + ")";
	}
}
