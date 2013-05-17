package dna.graph.undirected;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesAl;

public class UndirectedGraphAlHs extends UndirectedGraph {

	public UndirectedGraphAlHs(
			String name,
			long timestamp,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesAl<UndirectedNode, UndirectedEdge>(
				nodes), new EdgesHs<UndirectedEdge>(edges));
	}

}
