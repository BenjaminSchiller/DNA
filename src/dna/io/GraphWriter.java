package dna.io;

import java.io.IOException;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.io.etc.Keywords;

public class GraphWriter<G extends Graph<N, E>, N extends Node<E>, E extends Edge> {

	public boolean write(Graph<N, E> g, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(Keywords.graphGraph);
			writer.writeln(g.getName());
			writer.writeln(g.getGraphDatastructures().getGraphType());

			writer.writeKeyword(Keywords.graphNodes);
			writer.writeln(g.getNodeCount());
			writer.writeln(g.getGraphDatastructures().getNodeType());

			writer.writeKeyword(Keywords.graphEdges);
			writer.writeln(g.getEdgeCount());
			writer.writeln(g.getGraphDatastructures().getEdgeType());

			writer.writeKeyword(Keywords.graphTimestamp);
			writer.writeln(g.getTimestamp());

			writer.writeKeyword(Keywords.graphListOfNodes);
			for (N n : g.getNodes()) {
				writer.writeln(n.getStringRepresentation());
			}

			writer.writeKeyword(Keywords.graphListOfEdges);
			for (E e : g.getEdges()) {
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
