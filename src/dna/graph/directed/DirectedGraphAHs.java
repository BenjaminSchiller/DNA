package dna.graph.directed;

import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesA;

public class DirectedGraphAHs extends DirectedGraph {

	public DirectedGraphAHs(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesA<DirectedNode, DirectedEdge>(nodes),
				new EdgesHs<DirectedEdge>(edges));
	}

}
