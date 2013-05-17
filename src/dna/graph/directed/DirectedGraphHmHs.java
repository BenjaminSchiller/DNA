package dna.graph.directed;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesHm;

public class DirectedGraphHmHs extends DirectedGraph {

	public DirectedGraphHmHs(String name, long timestamp,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesHm<DirectedNode, DirectedEdge>(
				nodes), new EdgesHs<DirectedEdge>(edges));
	}

}
