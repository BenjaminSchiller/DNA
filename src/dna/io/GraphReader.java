package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.io.etc.Keywords;

public class GraphReader {

	public Graph read(String dir, String filename)
			throws ClassNotFoundException, IOException {
		return this.read(dir, filename, null);
	}

	@SuppressWarnings({ "rawtypes" })
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
			e.connectToNodes();
		}

		reader.close();
		return g;
	}
}
