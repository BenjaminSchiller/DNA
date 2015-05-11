package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure.ListType;

/**
 * 
 * in addition to V and E, operation counts for the vertex adjacency list (adj)
 * in undirected graphs is provided.
 * 
 * @author benni
 *
 */
public class OperationCountsUndirected extends OperationCounts {

	public OperationCount adj = new OperationCount(ListType.LocalEdgeList);

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("V: " + V + "\n");
		buff.append("E: " + E + "\n");
		buff.append("adj: " + adj + "\n");
		return buff.toString();
	}

	@Override
	public String getValues() {
		StringBuffer buff = new StringBuffer();
		buff.append("V\n" + V.getValues() + "\n");
		buff.append("E\n" + E.getValues() + "\n");
		buff.append("adj\n" + adj.getValues());
		return buff.toString();
	}

	@Override
	public void writeValues(String dir) throws IOException {
		V.writeValues(dir, "V" + suffix);
		E.writeValues(dir, "E" + suffix);
		adj.writeValues(dir, "adj" + suffix);
	}

	@Override
	public void setSizes(Graph g) {
		int nodes = g.getNodeCount();
		int edges = g.getEdgeCount();

		this.V.listCount = 1;
		this.V.listSize = nodes;
		this.E.listCount = 1;
		this.E.listSize = edges;

		this.adj.listCount = nodes;
		this.adj.listSize = (int) 2.0 * edges / nodes;
	}

}
