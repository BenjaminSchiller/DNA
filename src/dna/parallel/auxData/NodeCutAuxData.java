package dna.parallel.auxData;

import dna.graph.datastructures.GraphDataStructure;
import dna.parallel.partition.NodeCutPartition;
import dna.parallel.partition.Partition.PartitionType;

public class NodeCutAuxData extends AuxData<NodeCutPartition> {
	public NodeCutAuxData(GraphDataStructure gds) {
		super(PartitionType.NodeCut, gds, null);
	}

	public String toString() {
		return null;
	}

	@Override
	public void write(String dir, String filename) {
		// TODO Auto-generated method stub

	}

	public static NodeCutAuxData read(String dir, String filename) {
		return null;
	}

	@Override
	public void add(AuxData<NodeCutPartition> add) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(AuxData<NodeCutPartition> remove) {
		// TODO Auto-generated method stub
	}
}
