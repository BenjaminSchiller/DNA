package dna.parallel.auxData;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.io.Reader;
import dna.io.Writer;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.partition.SeparatedPartition;

/**
 * 
 * This extension of AuxData holds the auxiliary data for non-overlapping
 * partitions. It adds a single set of all edges between partitions which are
 * not parts of the partitions.
 * 
 * @author benni
 *
 */
public class SeparatedAuxData extends AuxData<SeparatedPartition> {

	public Set<Edge> bridges;

	public SeparatedAuxData(GraphDataStructure gds,
			Set<Node>[] nodesOfPartitions, Set<Edge> bridges) {
		super(PartitionType.Separated, gds, nodesOfPartitions);
		this.bridges = bridges;
	}

	public SeparatedAuxData(GraphDataStructure gds, int partitionCount) {
		super(PartitionType.Separated, gds, partitionCount);
		this.bridges = new HashSet<Edge>();
	}

	public int getEdgeCount() {
		return this.bridges.size();
	}

	public String toString() {
		return "Separated: " + this.bridges.size() + " edges between "
				+ this.getPartitionCount() + " partitions with "
				+ this.getNodeCount() + " nodes";
	}

	@Override
	public void write(String dir, String filename) {
		try {
			Writer w = new Writer(dir, filename);
			for (int i = 0; i < this.nodes.length; i++) {
				w.writeln(i + sep0 + this.getNodesString(this.nodes[i]));
			}
			w.writeln(this.getEdgesString(this.bridges));
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SeparatedAuxData read(GraphDataStructure gds,
			int partitionCount, String dir, String filename) throws IOException {
		Reader r = new Reader(dir, filename);
		SeparatedAuxData aux = new SeparatedAuxData(gds, partitionCount);
		for (int i = 0; i < partitionCount; i++) {
			String line = r.readString();
			String[] temp = line.split(sep0, 100);
			aux.nodes[i] = aux.getNodes(temp[1]);
		}
		aux.bridges = aux.getEdges(r.readString());
		r.close();
		return aux;
	}

	@Override
	public void add(AuxData<SeparatedPartition> add_) {
		SeparatedAuxData add = (SeparatedAuxData) add_;
		for (int i = 0; i < this.nodes.length; i++) {
			this.nodes[i].addAll(add.nodes[i]);
			for (Node n : add.nodes[i]) {
				this.mapping.put(n, i);
			}
		}
		this.bridges.addAll(add.bridges);
	}

	@Override
	public void remove(AuxData<SeparatedPartition> remove_) {
		SeparatedAuxData remove = (SeparatedAuxData) remove_;
		for (int i = 0; i < this.nodes.length; i++) {
			this.nodes[i].removeAll(remove.nodes[i]);
			for (Node n : remove.nodes[i]) {
				this.mapping.remove(n);
			}
		}
		this.bridges.removeAll(remove.bridges);
	}
}
