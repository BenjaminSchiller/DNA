package IO;

import java.io.IOException;

import Graph.Graph;
import Graph.IElement;
import Utils.Keywords;

public class GraphWriter {

	public boolean write(Graph g, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(Keywords.graphGraph);
			writer.writeln(g.getName());
			writer.writeln(g.getGraphDatastructures().getDataStructures());

			writer.writeKeyword(Keywords.graphNodes);
			writer.writeln(g.getNodeCount());

			writer.writeKeyword(Keywords.graphEdges);
			writer.writeln(g.getEdgeCount());

			writer.writeKeyword(Keywords.graphTimestamp);
			writer.writeln(g.getTimestamp());

			writer.writeKeyword(Keywords.graphListOfNodes);
			for (IElement n : g.getNodes()) {
				writer.writeln(n.getStringRepresentation());
			}

			writer.writeKeyword(Keywords.graphListOfEdges);
			for (IElement e : g.getEdges()) {
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
