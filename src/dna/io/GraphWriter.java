package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.util.Config;

public class GraphWriter {

	public static boolean write(Graph g, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_NAME"));
			writer.writeln(g.getName());

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_DATASTRUCTURES"));
			writer.writeln(g.getGraphDatastructures().getDataStructures());

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_NODE_COUNT"));
			writer.writeln(g.getNodeCount());

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_EDGE_COUNT"));
			writer.writeln(g.getEdgeCount());

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_TIMESTAMP"));
			writer.writeln(g.getTimestamp());

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_NODES_LIST"));
			for (IElement n : g.getNodes()) {
				if (n == null)
					continue;
				writer.writeln(n.getStringRepresentation());
			}

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_EDGES_LIST"));
			for (IElement e : g.getEdges()) {
				if (e == null)
					continue;
				writer.writeln(e.getStringRepresentation());
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
