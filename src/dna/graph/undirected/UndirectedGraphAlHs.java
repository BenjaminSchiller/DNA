package dna.graph.undirected;

import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesAl;

public class UndirectedGraphAlHs extends UndirectedGraph {

	public UndirectedGraphAlHs(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesAl<UndirectedNode, UndirectedEdge>(
				nodes), new EdgesHs<UndirectedEdge>(edges));
	}

}
