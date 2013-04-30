package dna.graph.directed;

import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesAl;

public class DirectedGraphAlAl extends DirectedGraph {

	public DirectedGraphAlAl(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesAl<DirectedNode, DirectedEdge>(nodes),
				new EdgesAl<DirectedEdge>(edges));
	}

}
