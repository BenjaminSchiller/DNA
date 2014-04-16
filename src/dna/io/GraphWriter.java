package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.IElement;

public class GraphWriter {

	public static final String nameKeyword = "DNA Graph";
	public static final String datastructuresKeyword = "Data Structures";

	public static final String nodeCountKeyword = "Nodes";
	public static final String edgeCountKeyword = "Edges";

	public static final String timestampKeyword = "Timestamp";

	public static final String nodesListKeyword = "List of Nodes";
	public static final String edgesListKeyword = "List of Edges";

	public static boolean write(Graph g, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(GraphWriter.nameKeyword);
			writer.writeln(g.getName());

			writer.writeKeyword(GraphWriter.datastructuresKeyword);
			writer.writeln(g.getGraphDatastructures().getDataStructures());

			writer.writeKeyword(GraphWriter.nodeCountKeyword);
			writer.writeln(g.getNodeCount());

			writer.writeKeyword(GraphWriter.edgeCountKeyword);
			writer.writeln(g.getEdgeCount());

			writer.writeKeyword(GraphWriter.timestampKeyword);
			writer.writeln(g.getTimestamp());

			writer.writeKeyword(GraphWriter.nodesListKeyword);
			for (IElement n : g.getNodes()) {
				if (n == null)
					continue;
				writer.writeln(n.asString());
			}

			writer.writeKeyword(GraphWriter.edgesListKeyword);
			for (IElement e : g.getEdges()) {
				if (e == null)
					continue;
				writer.writeln(e.asString());
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
