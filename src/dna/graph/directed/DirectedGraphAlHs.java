package dna.graph.directed;

import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesAl;

public class DirectedGraphAlHs extends DirectedGraph {

	public DirectedGraphAlHs(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesAl<DirectedNode, DirectedEdge>(nodes),
				new EdgesHs<DirectedEdge>(edges));
	}

	// private HashSet<DirectedEdge> edges;
	//
	// public DirectedGraphAlHs(String name, long timestamp, int nodes, int
	// edges) {
	// super(name, timestamp, nodes);
	// this.edges = new HashSet<DirectedEdge>(edges);
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
	// // TODO implement random directed edge @hs
	// return null;
	// }

}
