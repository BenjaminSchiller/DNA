package dna.graph.directed;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesHm;

public class DirectedGraphHmAl extends DirectedGraph {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DirectedGraphHmAl(String name, long timestamp,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesHm<DirectedNode, DirectedEdge>(
				nodes, (GraphDatastructures) ds), new EdgesAl<DirectedEdge>(
				edges));
	}

}
