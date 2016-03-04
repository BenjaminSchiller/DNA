package dna.parallel.auxData;

import java.io.IOException;
import java.util.Set;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.io.Reader;
import dna.io.Writer;
import dna.parallel.partition.OverlappingPartition;
import dna.parallel.partition.Partition.PartitionType;

/**
 * 
 * This extension of AuxData holds the auxiliary data for overlapping
 * partitions. For each partition, it adds a set of neighbors which are
 * contained in the overlap.
 * 
 * @author benni
 *
 */
public class OverlappingAuxData extends AuxData<OverlappingPartition> {
	public Set<Node>[] neighbors;

	public boolean addNeighbor(int index, Node n) {
		return this.neighbors[index].add(n);
	}

	public boolean hasNeighbor(int index, Node n) {
		return this.neighbors[index].contains(n);
	}

	public OverlappingAuxData(GraphDataStructure gds,
			Set<Node>[] nodesOfPartitions, Set<Node>[] neighbors) {
		super(PartitionType.Overlapping, gds, nodesOfPartitions);
		this.neighbors = neighbors;
	}

	public OverlappingAuxData(GraphDataStructure gds, int partitionCount) {
		super(PartitionType.Overlapping, gds, partitionCount);
		this.neighbors = getInitialNodes(partitionCount);
	}

	public int getDuplicateCount() {
		int sum = 0;
		for (Set<Node> s : this.neighbors) {
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
			for (int i = 0; i < this.nodes.length; i++) {

				w.writeln(i + sep0 + this.getNodesString(this.nodes[i]) + sep0
						+ this.getNodesString(this.neighbors[i]));
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
			aux.nodes[i] = aux.getNodes(temp[1]);
			aux.neighbors[i] = aux.getNodes(temp[2]);
			for (Node n : aux.nodes[i]) {
				aux.mapping.put(n, i);
			}
		}
		r.close();
		return aux;
	}

	@Override
	public void add(AuxData<OverlappingPartition> add_) {
		OverlappingAuxData add = (OverlappingAuxData) add_;
		for (int i = 0; i < this.nodes.length; i++) {
			this.nodes[i].addAll(add.nodes[i]);
			for (Node n : add.nodes[i]) {
				this.mapping.put(n, i);
			}
			this.neighbors[i].addAll(add.neighbors[i]);
		}
	}

	@Override
	public void remove(AuxData<OverlappingPartition> remove_) {
		OverlappingAuxData remove = (OverlappingAuxData) remove_;
		for (int i = 0; i < this.nodes.length; i++) {
			this.nodes[i].removeAll(remove.nodes[i]);
			for (Node n : remove.nodes[i]) {
				this.mapping.remove(n);
			}
			this.neighbors[i].removeAll(remove.neighbors[i]);
		}
	}
}
