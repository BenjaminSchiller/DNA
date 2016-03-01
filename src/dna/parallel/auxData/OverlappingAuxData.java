package dna.parallel.auxData;

import java.io.IOException;
import java.util.Set;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.io.Reader;
import dna.io.Writer;
import dna.parallel.partition.OverlappingPartition;
import dna.parallel.partition.Partition.PartitionType;

public class OverlappingAuxData extends AuxData<OverlappingPartition> {
	public Set<Node>[] neighborsOfPartitions;

	public OverlappingAuxData(GraphDataStructure gds,
			Set<Node>[] nodesOfPartitions, Set<Node>[] neighborsOfPartitions) {
		super(PartitionType.Overlapping, gds, nodesOfPartitions);
		this.neighborsOfPartitions = neighborsOfPartitions;
	}

	public OverlappingAuxData(GraphDataStructure gds, int partitionCount) {
		super(PartitionType.Overlapping, gds, partitionCount);
		this.neighborsOfPartitions = getInitialNodes(partitionCount);
	}

	public int getDuplicateCount() {
		int sum = 0;
		for (Set<Node> s : this.neighborsOfPartitions) {
			sum += s.size();
		}
		return sum;
	}

	public String toString() {
		return "Overlapping: " + this.getDuplicateCount() + " duplicates in "
				+ this.getPartitionCount() + " partitions with "
				+ this.getNodeCount() + " nodes";
	}

	@Override
	public void write(String dir, String filename) {
		try {
			Writer w = new Writer(dir, filename);
			for (int i = 0; i < this.nodesOfPartitions.length; i++) {

				w.writeln(i + sep0
						+ this.getNodesString(this.nodesOfPartitions[i]) + sep0
						+ this.getNodesString(this.neighborsOfPartitions[i]));
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static OverlappingAuxData read(GraphDataStructure gds,
			int partitionCount, String dir, String filename) throws IOException {
		Reader r = new Reader(dir, filename);
		OverlappingAuxData aux = new OverlappingAuxData(gds, partitionCount);
		for (int i = 0; i < partitionCount; i++) {
			String line = r.readString();
			String[] temp = line.split(sep0, 100);
			aux.nodesOfPartitions[i] = aux.getNodes(temp[1]);
			aux.neighborsOfPartitions[i] = aux.getNodes(temp[2]);
		}
		r.close();
		return aux;
	}

	@Override
	public void add(AuxData<OverlappingPartition> add_) {
		OverlappingAuxData add = (OverlappingAuxData) add_;
		for (int i = 0; i < this.nodesOfPartitions.length; i++) {
			this.nodesOfPartitions[i].addAll(add.nodesOfPartitions[i]);
			for (Node n : add.nodesOfPartitions[i]) {
				this.mapping.put(n, i);
			}
			this.neighborsOfPartitions[i].addAll(add.neighborsOfPartitions[i]);
		}
	}

	@Override
	public void remove(AuxData<OverlappingPartition> remove_) {
		OverlappingAuxData remove = (OverlappingAuxData) remove_;
		for (int i = 0; i < this.nodesOfPartitions.length; i++) {
			this.nodesOfPartitions[i].removeAll(remove.nodesOfPartitions[i]);
			for (Node n : remove.nodesOfPartitions[i]) {
				this.mapping.remove(n);
			}
			this.neighborsOfPartitions[i]
					.removeAll(remove.neighborsOfPartitions[i]);
		}
	}
}
