package dna.graph.undirected;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesHs;
import dna.graph.nodes.NodesAl;

public class UndirectedGraphAlHs extends UndirectedGraph {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public UndirectedGraphAlHs(
			String name,
			long timestamp,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesAl<UndirectedNode, UndirectedEdge>(
				nodes, (GraphDatastructures) ds), new EdgesHs<UndirectedEdge>(
				edges));
	}

}
