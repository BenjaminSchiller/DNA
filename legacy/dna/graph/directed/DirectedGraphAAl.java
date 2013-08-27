package dna.graph.directed;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesA;

public class DirectedGraphAAl extends DirectedGraph {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DirectedGraphAAl(String name, long timestamp,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesA<DirectedNode, DirectedEdge>(
				nodes, (GraphDatastructures) ds), new EdgesAl<DirectedEdge>(
				edges));
	}

}
