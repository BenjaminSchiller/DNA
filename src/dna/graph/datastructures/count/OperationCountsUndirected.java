package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.IGraph;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.count.OperationCount.AggregationType;
import dna.io.Reader;
import dna.io.Writer;

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

	// @Override
	// public String getValues() {
	// StringBuffer buff = new StringBuffer();
	// buff.append("V\n" + V.getValues() + "\n");
	// buff.append("E\n" + E.getValues() + "\n");
	// buff.append("adj\n" + adj.getValues());
	// return buff.toString();
	// }

	@Override
	public void writeValues(String dir) throws IOException {
		V.writeValues(dir, "V.dat");
		E.writeValues(dir, "E.dat");
		adj.writeValues(dir, "adj.dat");
	}

	@Override
	public void readValues(String dir) throws IOException {
		V = OperationCount.read(dir, "V.dat", ListType.GlobalNodeList);
		E = OperationCount.read(dir, "E.dat", ListType.GlobalEdgeList);
		adj = OperationCount.read(dir, "adj.dat", ListType.LocalEdgeList);
	}

	@Override
	public void writeValues(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);
		V.writeValues(w, prefixV);
		E.writeValues(w, prefixE);
		adj.writeValues(w, prefixAdj);
		w.close();
	}

	@Override
	public void readValues(String dir, String filename) throws IOException {
		Reader r = Reader.getReader(dir, filename);
		V = OperationCount.read(r, prefixV, ListType.GlobalNodeList);
		E = OperationCount.read(r, prefixE, ListType.GlobalEdgeList);
		adj = OperationCount.read(r, prefixAdj, ListType.LocalEdgeList);
		r.close();
	}

	@Override
	public void setSizes(IGraph g) {
		int nodes = g.getNodeCount();
		int edges = g.getEdgeCount();

		this.V.listCount = 1;
		this.V.listSize = nodes;
		this.E.listCount = 1;
		this.E.listSize = edges;

		this.adj.listCount = nodes;
		this.adj.listSize = (int) 2.0 * edges / nodes;
	}

	@Override
	public OperationCounts add(AggregationType at, OperationCounts... ocs) {
		OperationCount[] ocs_V = new OperationCount[ocs.length];
		OperationCount[] ocs_E = new OperationCount[ocs.length];
		OperationCount[] ocs_adj = new OperationCount[ocs.length];
		for (int i = 0; i < ocs.length; i++) {
			ocs_V[i] = ocs[i].V;
			ocs_E[i] = ocs[i].E;
			ocs_adj[i] = ((OperationCountsUndirected) ocs[i]).adj;
		}
		OperationCountsUndirected oc = new OperationCountsUndirected();
		oc.V = OperationCount.add(at, ocs_V);
		oc.E = OperationCount.add(at, ocs_E);
		oc.adj = OperationCount.add(at, ocs_adj);
		return oc;
	}

}
