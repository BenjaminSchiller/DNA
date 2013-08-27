package dna.io;

import java.io.IOException;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraphDatastructures;
import dna.io.etc.Keywords;
import dna.util.Log;

public class GraphReader<G extends Graph<N, E>, N extends Node<E>, E extends Edge> {

	public Graph<N, E> read(String dir, String filename)
			throws ClassNotFoundException, IOException {
		return this.read(dir, filename, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Graph<N, E> read(String dir, String filename,
			GraphDatastructures<G, N, E> ds) throws IOException,
			ClassNotFoundException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(Keywords.graphGraph);
		String name = reader.readString();
		Class gClass = reader.readClass();

		reader.readKeyword(Keywords.graphNodes);
		int nodes = reader.readInt();
		Class nClass = reader.readClass();

		reader.readKeyword(Keywords.graphEdges);
		int edges = reader.readInt();
		Class eClass = reader.readClass();

		reader.readKeyword(Keywords.graphTimestamp);
		long timestamp = reader.readLong();

		if (ds == null) {
			if (DirectedEdge.class.isAssignableFrom(eClass)) {
				ds = (GraphDatastructures<G, N, E>) new DirectedGraphDatastructures(
						gClass, nClass, eClass);
			} else if (UndirectedEdge.class.isAssignableFrom(eClass)) {
				ds = (GraphDatastructures<G, N, E>) new UndirectedGraphDatastructures(
						gClass, nClass, eClass);
			} else {
				Log.error("no DS given but '" + eClass
						+ "' not implemented yet");
				return null;
			}
		}

		System.out.println("DS:\n" + ds);

		G g = ds.newGraphInstance(name, timestamp, nodes, edges);

		reader.readKeyword(Keywords.graphListOfNodes);
		String line = null;
		while (!(line = reader.readString()).equals(Keywords
				.asLine(Keywords.graphListOfEdges))) {
			g.addNode(ds.newNodeInstance(line));
		}

		while ((line = reader.readString()) != null) {
			E e = ds.newEdgeInstance(line, g);
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
