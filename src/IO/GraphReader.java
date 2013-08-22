package IO;

import java.io.IOException;

import Utils.Keywords;
import DataStructures.GraphDataStructure;
import Graph.Graph;
import Graph.Edges.DirectedEdge;
import Graph.Edges.Edge;
import Graph.Edges.UndirectedEdge;

public class GraphReader {

	public Graph read(String dir, String filename)
			throws ClassNotFoundException, IOException {
		return this.read(dir, filename, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Graph read(String dir, String filename,
			GraphDataStructure ds) throws IOException,
			ClassNotFoundException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(Keywords.graphGraph);
		String name = reader.readString();
		
		reader.readKeyword(Keywords.graphDataStructures);
		String gdsString = reader.readString();
		
		if (ds == null) {
			ds = new GraphDataStructure(gdsString);
		}

		reader.readKeyword(Keywords.graphNodes);
		int nodes = reader.readInt();

		reader.readKeyword(Keywords.graphEdges);
		int edges = reader.readInt();

		reader.readKeyword(Keywords.graphTimestamp);
		long timestamp = reader.readLong();

		Graph g = ds.newGraphInstance(name, timestamp, nodes, edges);
		Class eClass = ds.getEdgeType();

		reader.readKeyword(Keywords.graphListOfNodes);
		String line = null;
		while (!(line = reader.readString()).equals(Keywords
				.asLine(Keywords.graphListOfEdges))) {
			g.addNode(ds.newNodeInstance(line));
		}

		while ((line = reader.readString()) != null) {
			Edge e = ds.newEdgeInstance(line, g);
			g.addEdge(e);
			if (DirectedEdge.class.isAssignableFrom(eClass)) {
				DirectedEdge d = (DirectedEdge) e;
				d.getSrc().addEdge(d);
				d.getDst().addEdge(d);
			} else if (UndirectedEdge.class.isAssignableFrom(eClass)) {
				UndirectedEdge u = (UndirectedEdge) e;
				u.getNode1().addEdge(u);
				u.getNode2().addEdge(u);
			}
		}

		reader.close();
		return g;
	}
}
