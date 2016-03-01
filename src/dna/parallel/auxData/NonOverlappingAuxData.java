package dna.parallel.auxData;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.io.Reader;
import dna.io.Writer;
import dna.parallel.partition.NonOverlappingPartition;
import dna.parallel.partition.Partition.PartitionType;

public class NonOverlappingAuxData extends AuxData<NonOverlappingPartition> {

	public Set<Edge> edgesBetweenPartitions;

	public NonOverlappingAuxData(GraphDataStructure gds,
			Set<Node>[] nodesOfPartitions, Set<Edge> edgesBetweenPartitions) {
		super(PartitionType.NonOverlapping, gds, nodesOfPartitions);
		this.edgesBetweenPartitions = edgesBetweenPartitions;
	}

	public NonOverlappingAuxData(GraphDataStructure gds, int partitionCount) {
		super(PartitionType.NonOverlapping, gds, partitionCount);
		this.edgesBetweenPartitions = new HashSet<Edge>();
	}

	public int getEdgeCount() {
		return this.edgesBetweenPartitions.size();
	}

	public String toString() {
		return "NonOverlapping: " + this.edgesBetweenPartitions.size()
				+ " edges between " + this.getPartitionCount()
				+ " partitions with " + this.getNodeCount() + " nodes";
	}

	@Override
	public void write(String dir, String filename) {
		try {
			Writer w = new Writer(dir, filename);
			for (int i = 0; i < this.nodesOfPartitions.length; i++) {
				w.writeln(i + sep0
						+ this.getNodesString(this.nodesOfPartitions[i]));
			}
			w.writeln(this.getEdgesString(this.edgesBetweenPartitions));
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static NonOverlappingAuxData read(GraphDataStructure gds,
			int partitionCount, String dir, String filename) throws IOException {
		Reader r = new Reader(dir, filename);
		NonOverlappingAuxData aux = new NonOverlappingAuxData(gds,
				partitionCount);
		for (int i = 0; i < partitionCount; i++) {
			String line = r.readString();
			String[] temp = line.split(sep0, 100);
			aux.nodesOfPartitions[i] = aux.getNodes(temp[1]);
		}
		aux.edgesBetweenPartitions = aux.getEdges(r.readString());
		r.close();
		return aux;
	}

	@Override
	public void add(AuxData<NonOverlappingPartition> add_) {
		NonOverlappingAuxData add = (NonOverlappingAuxData) add_;
		for (int i = 0; i < this.nodesOfPartitions.length; i++) {
			this.nodesOfPartitions[i].addAll(add.nodesOfPartitions[i]);
			for (Node n : add.nodesOfPartitions[i]) {
				this.mapping.put(n, i);
			}
		}
		this.edgesBetweenPartitions.addAll(add.edgesBetweenPartitions);
	}

	@Override
	public void remove(AuxData<NonOverlappingPartition> remove_) {
		NonOverlappingAuxData remove = (NonOverlappingAuxData) remove_;
		for (int i = 0; i < this.nodesOfPartitions.length; i++) {
			this.nodesOfPartitions[i].removeAll(remove.nodesOfPartitions[i]);
			for (Node n : remove.nodesOfPartitions[i]) {
				this.mapping.remove(n);
			}
		}
		this.edgesBetweenPartitions.removeAll(remove.edgesBetweenPartitions);
	}
}
