package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;

public class GraphReader {

	public static Graph read(String dir, String filename)
			throws ClassNotFoundException, IOException {
		return read(dir, filename, null);
	}

	public static Graph read(String dir, String filename, GraphDataStructure ds)
			throws IOException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(GraphWriter.nameKeyword);
		String name = reader.readString();

		reader.readKeyword(GraphWriter.datastructuresKeyword);
		String gdsString = reader.readString();

		if (ds == null) {
			ds = new GraphDataStructure(gdsString);
		}

		reader.readKeyword(GraphWriter.nodeCountKeyword);
		int nodes = reader.readInt();

		reader.readKeyword(GraphWriter.edgeCountKeyword);
		int edges = reader.readInt();

		reader.readKeyword(GraphWriter.timestampKeyword);
		long timestamp = reader.readLong();

		Graph g = ds.newGraphInstance(name, timestamp, nodes, edges);

		reader.readKeyword(GraphWriter.nodesListKeyword);
		String line = null;
		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(GraphWriter.edgesListKeyword))) {
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

	public static String readName(String dir, String filename)
			throws IOException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(GraphWriter.nameKeyword);
		String name = reader.readString();

		reader.close();
		return name;
	}

	public static GraphDataStructure readGDS(String dir, String filename)
			throws IOException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(GraphWriter.nameKeyword);
		String name = reader.readString();

		reader.readKeyword(GraphWriter.datastructuresKeyword);
		String gdsString = reader.readString();

		GraphDataStructure gds = new GraphDataStructure(gdsString);

		reader.close();
		return gds;
	}

}
