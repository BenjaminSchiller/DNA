package IO;

import java.io.IOException;

import Graph.IElement;
import Graph.ReadableGraph;
import Utils.Keywords;

public class GraphWriter {

	public boolean write(ReadableGraph g, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(Keywords.graphGraph);
			writer.writeln(g.getName());
			
			writer.writeKeyword(Keywords.graphDataStructures);
			writer.writeln(g.getGraphDatastructures().getDataStructures());

			writer.writeKeyword(Keywords.graphNodes);
			writer.writeln(g.getNodeCount());

			writer.writeKeyword(Keywords.graphEdges);
			writer.writeln(g.getEdgeCount());

			writer.writeKeyword(Keywords.graphTimestamp);
			writer.writeln(g.getTimestamp());

			writer.writeKeyword(Keywords.graphListOfNodes);
			for (IElement n : g.getNodes()) {
				if ( n == null ) continue;
				writer.writeln(n.getStringRepresentation());
			}

			writer.writeKeyword(Keywords.graphListOfEdges);
			for (IElement e : g.getEdges()) {
				if ( e == null ) continue;
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
