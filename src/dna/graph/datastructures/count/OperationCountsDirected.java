package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure.ListType;

/**
 * 
 * in addition to V and E, operation counts for the vertex in- and out-adjacency
 * lists (in, out) as well as the neighborhoos in directed graphs is provided
 * 
 * @author benni
 *
 */
public class OperationCountsDirected extends OperationCounts {

	public OperationCount in = new OperationCount(ListType.LocalInEdgeList);
	public OperationCount out = new OperationCount(ListType.LocalOutEdgeList);
	public OperationCount neighbors = new OperationCount(ListType.LocalNodeList);

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("V: " + V + "\n");
		buff.append("E: " + E + "\n");
		buff.append("in: " + in + "\n");
		buff.append("out: " + out + "\n");
		buff.append("neighbors: " + neighbors);
		return buff.toString();
	}

	@Override
	public String getValues() {
		StringBuffer buff = new StringBuffer();
		buff.append("V\n" + V.getValues() + "\n");
		buff.append("E\n" + E.getValues() + "\n");
		buff.append("in\n" + in.getValues() + "\n");
		buff.append("out\n" + out.getValues() + "\n");
		buff.append("neighbors\n" + neighbors.getValues());
		return buff.toString();
	}

	@Override
	public void writeValues(String dir) throws IOException {
		V.writeValues(dir, "V" + suffix);
		E.writeValues(dir, "E" + suffix);
		in.writeValues(dir, "in" + suffix);
		out.writeValues(dir, "out" + suffix);
		neighbors.writeValues(dir, "neighbors" + suffix);
	}

	@Override
	public void setSizes(Graph g) {
		int nodes = g.getNodeCount();
		int edges = g.getEdgeCount();

		this.V.listCount = 1;
		this.V.listSize = nodes;
		this.E.listCount = 1;
		this.E.listSize = edges;

		this.in.listCount = nodes;
		this.in.listSize = (int) 2.0 * edges / nodes;
		this.out.listCount = nodes;
		this.out.listSize = (int) 2.0 * edges / nodes;
		this.neighbors.listCount = nodes;
		this.neighbors.listSize = (int) 2.0 * edges / nodes;
	}

}
