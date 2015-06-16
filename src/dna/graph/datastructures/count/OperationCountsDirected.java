package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.count.OperationCount.AggregationType;
import dna.io.Reader;
import dna.io.Writer;

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

	// @Override
	// public String getValues() {
	// StringBuffer buff = new StringBuffer();
	// buff.append("V\n" + V.getValues() + "\n");
	// buff.append("E\n" + E.getValues() + "\n");
	// buff.append("in\n" + in.getValues() + "\n");
	// buff.append("out\n" + out.getValues() + "\n");
	// buff.append("neighbors\n" + neighbors.getValues());
	// return buff.toString();
	// }

	@Override
	public void writeValues(String dir) throws IOException {
		V.writeValues(dir, "V.dat");
		E.writeValues(dir, "E.dat");
		in.writeValues(dir, "in.dat");
		out.writeValues(dir, "out.dat");
		neighbors.writeValues(dir, "neighbors.dat");
	}

	@Override
	public void readValues(String dir) throws IOException {
		V = OperationCount.read(dir, "V.dat", ListType.GlobalNodeList);
		E = OperationCount.read(dir, "E.dat", ListType.GlobalEdgeList);
		in = OperationCount.read(dir, "in.dat", ListType.LocalInEdgeList);
		out = OperationCount.read(dir, "out.dat", ListType.LocalOutEdgeList);
		neighbors = OperationCount.read(dir, "neighbors.dat",
				ListType.LocalNodeList);
	}

	@Override
	public void writeValues(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);
		V.writeValues(w, prefixV);
		E.writeValues(w, prefixE);
		in.writeValues(w, prefixIn);
		out.writeValues(w, prefixOut);
		neighbors.writeValues(w, prefixNeighbors);
		w.close();
	}

	@Override
	public void readValues(String dir, String filename) throws IOException {
		Reader r = Reader.getReader(dir, filename);
		V = OperationCount.read(r, prefixV, ListType.GlobalNodeList);
		E = OperationCount.read(r, prefixE, ListType.GlobalEdgeList);
		in = OperationCount.read(r, prefixIn, ListType.LocalInEdgeList);
		out = OperationCount.read(r, prefixOut, ListType.LocalOutEdgeList);
		neighbors = OperationCount.read(r, prefixNeighbors,
				ListType.LocalNodeList);
		r.close();
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

	@Override
	public OperationCounts add(AggregationType at, OperationCounts... ocs) {
		OperationCount[] ocs_V = new OperationCount[ocs.length];
		OperationCount[] ocs_E = new OperationCount[ocs.length];
		OperationCount[] ocs_in = new OperationCount[ocs.length];
		OperationCount[] ocs_out = new OperationCount[ocs.length];
		OperationCount[] ocs_neighbors = new OperationCount[ocs.length];
		for (int i = 0; i < ocs.length; i++) {
			ocs_V[i] = ocs[i].V;
			ocs_E[i] = ocs[i].E;
			ocs_in[i] = ((OperationCountsDirected) ocs[i]).in;
			ocs_out[i] = ((OperationCountsDirected) ocs[i]).out;
			ocs_neighbors[i] = ((OperationCountsDirected) ocs[i]).neighbors;
		}
		OperationCountsDirected oc = new OperationCountsDirected();
		oc.V = OperationCount.add(at, ocs_V);
		oc.E = OperationCount.add(at, ocs_E);
		oc.in = OperationCount.add(at, ocs_in);
		oc.out = OperationCount.add(at, ocs_out);
		oc.neighbors = OperationCount.add(at, ocs_neighbors);
		return oc;
	}

}
