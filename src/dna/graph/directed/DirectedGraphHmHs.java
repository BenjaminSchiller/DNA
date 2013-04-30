package dna.graph.directed;

import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesHm;

public class DirectedGraphHmHs extends DirectedGraph {

	public DirectedGraphHmHs(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesHm<DirectedNode, DirectedEdge>(nodes),
				new EdgesHs<DirectedEdge>(edges));
	}

}
