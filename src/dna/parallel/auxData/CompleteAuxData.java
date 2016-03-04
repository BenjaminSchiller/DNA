package dna.parallel.auxData;

import java.io.IOException;
import java.util.Set;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.io.Reader;
import dna.io.Writer;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;

public class CompleteAuxData extends AuxData<CompletePartition> {

	public CompleteAuxData(GraphDataStructure gds, Set<Node>[] nodes) {
		super(PartitionType.Complete, gds, nodes);
	}

	public CompleteAuxData(GraphDataStructure gds, int partitionCount) {
		super(PartitionType.Complete, gds, partitionCount);
	}

	@Override
	public void write(String dir, String filename) {
		try {
			Writer w = new Writer(dir, filename);
			for (int i = 0; i < this.nodes.length; i++) {
				w.writeln(i + sep0 + this.getNodesString(this.nodes[i]));
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CompleteAuxData read(GraphDataStructure gds,
			int partitionCount, String dir, String filename) throws IOException {
		Reader r = new Reader(dir, filename);
		CompleteAuxData aux = new CompleteAuxData(gds, partitionCount);
		for (int i = 0; i < partitionCount; i++) {
			String line = r.readString();
			String[] temp = line.split(sep0, 100);
			aux.nodes[i] = aux.getNodes(temp[1]);
		}
		r.close();
		return aux;
	}

	@Override
	public void add(AuxData<CompletePartition> add_) {
		CompleteAuxData add = (CompleteAuxData) add_;
		for (int i = 0; i < this.nodes.length; i++) {
			this.nodes[i].addAll(add.nodes[i]);
			for (Node n : add.nodes[i]) {
				this.mapping.put(n, i);
			}
		}
	}

	@Override
	public void remove(AuxData<CompletePartition> remove_) {
		CompleteAuxData remove = (CompleteAuxData) remove_;
		for (int i = 0; i < this.nodes.length; i++) {
			this.nodes[i].removeAll(remove.nodes[i]);
			for (Node n : remove.nodes[i]) {
				this.mapping.remove(n);
			}
		}
	}

}
