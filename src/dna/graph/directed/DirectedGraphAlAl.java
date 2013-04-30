package dna.graph.directed;

import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesAl;

public class DirectedGraphAlAl extends DirectedGraph {

	public DirectedGraphAlAl(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesAl<DirectedNode, DirectedEdge>(nodes),
				new EdgesAl<DirectedEdge>(edges));
	}

	// private ArrayList<DirectedEdge> edges;
	//
	// public DirectedGraphAlAl(String name, long timestamp, int nodes, int
	// edges) {
	// super(name, timestamp, nodes);
	// this.edges = new ArrayList<DirectedEdge>(edges);
	// }
	//
	// @Override
	// public int getEdgeCount() {
	// return this.edges.size();
	// }
	//
	// @Override
	// public Collection<DirectedEdge> getEdges() {
	// return this.edges;
	// }
	//
	// @Override
	// public boolean addEdge(DirectedEdge e) {
	// return e != null && this.edges.add(e);
	// }
	//
	// @Override
	// public boolean removeEdge(DirectedEdge e) {
	// return e != null && this.edges.remove(e);
	// }
	//
	// @Override
	// public boolean containsEdge(DirectedEdge e) {
	// return this.edges.contains(e);
	// }
	//
	// @Override
	// public DirectedEdge getRandomEdge() {
	// // TODO implement random directed edge @ al
	// return null;
	// }

}
