package dna.graph.directed;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesA;

public class DirectedGraphAHs extends DirectedGraph {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DirectedGraphAHs(String name, long timestamp,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesA<DirectedNode, DirectedEdge>(
				nodes, (GraphDatastructures) ds), new EdgesHs<DirectedEdge>(
				edges));
	}

}
