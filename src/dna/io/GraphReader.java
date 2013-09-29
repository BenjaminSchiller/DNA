package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.util.Config;

public class GraphReader {

	public Graph read(String dir, String filename)
			throws ClassNotFoundException, IOException {
		return this.read(dir, filename, null);
	}

	@SuppressWarnings({ "rawtypes" })
	public Graph read(String dir, String filename, GraphDataStructure ds)
			throws IOException, ClassNotFoundException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(Config.get("GRAPH_KEYWORD_NAME"));
		String name = reader.readString();

		reader.readKeyword(Config.get("GRAPH_KEYWORD_DATASTRUCTURES"));
		String gdsString = reader.readString();

		if (ds == null) {
			ds = new GraphDataStructure(gdsString);
		}

		reader.readKeyword(Config.get("GRAPH_KEYWORD_NODE_COUNT"));
		int nodes = reader.readInt();

		reader.readKeyword(Config.get("GRAPH_KEYWORD_EDGE_COUNT"));
		int edges = reader.readInt();

		reader.readKeyword(Config.get("GRAPH_KEYWORD_TIMESTAMP"));
		long timestamp = reader.readLong();

		Graph g = ds.newGraphInstance(name, timestamp, nodes, edges);
		Class eClass = ds.getEdgeType();

		reader.readKeyword(Config.get("GRAPH_KEYWORD_NODES_LIST"));
		String line = null;
		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(Config.get("GRAPH_KEYWORD_EDGES_LIST")))) {
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
