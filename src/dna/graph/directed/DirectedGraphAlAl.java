package dna.graph.directed;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesAl;

public class DirectedGraphAlAl extends DirectedGraph {

	public DirectedGraphAlAl(String name, long timestamp,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesAl<DirectedNode, DirectedEdge>(
				nodes), new EdgesAl<DirectedEdge>(edges));
	}

}
