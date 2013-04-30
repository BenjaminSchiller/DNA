package dna.graph.undirected;

import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesAl;

public class UndirectedGraphAlAl extends UndirectedGraph {

	public UndirectedGraphAlAl(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, new NodesAl<UndirectedNode, UndirectedEdge>(
				nodes), new EdgesAl<UndirectedEdge>(edges));
	}

}
