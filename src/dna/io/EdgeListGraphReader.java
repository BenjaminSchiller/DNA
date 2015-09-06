package dna.io;

import java.io.IOException;
import java.util.HashMap;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class EdgeListGraphReader {

	public static IGraph read(String dir, String filename, String separator,
			GraphDataStructure gds) throws IOException {

		HashMap<String, Integer> mapping = new HashMap<String, Integer>();
		int nodes = 0;
		int edges = 0;
		Reader reader = new Reader(dir, filename);
		String line = null;
		while ((line = reader.readString()) != null) {
			String[] temp = line.split(separator);
			if (temp[0].equals(temp[1])) {
				continue;
			}
			if (!mapping.containsKey(temp[0])) {
				mapping.put(temp[0], nodes++);
			}
			if (!mapping.containsKey(temp[1])) {
				mapping.put(temp[1], nodes++);
			}
			edges++;
		}
		reader.close();

		IGraph g = gds.newGraphInstance(filename, 0, nodes, edges);
		for (int i = 0; i < nodes; i++) {
			g.addNode(gds.newNodeInstance(i));
		}

		reader = new Reader(dir, filename);
		while ((line = reader.readString()) != null) {
			String[] temp = line.split(separator);
			if (temp[0].equals(temp[1])) {
				continue;
			}
			Node src = g.getNode(mapping.get(temp[0]));
			Node dst = g.getNode(mapping.get(temp[1]));
			Edge e = gds.newEdgeInstance(src, dst);
			g.addEdge(e);
			src.addEdge(e);
			dst.addEdge(e);
		}

		reader.close();
		return g;
	}

}
