package dna.graph.directed;

import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesAl;

public class DirectedGraphAlHs extends DirectedGraph {

	public DirectedGraphAlHs(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesAl<DirectedNode, DirectedEdge>(nodes),
				new EdgesHs<DirectedEdge>(edges));
	}

}
