package dna.graph.directed;

import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesA;

public class DirectedGraphAAl extends DirectedGraph {

	public DirectedGraphAAl(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesA<DirectedNode, DirectedEdge>(nodes),
				new EdgesAl<DirectedEdge>(edges));
	}

}
