package dna.graph.undirected;

import dna.graph.GraphDatastructures;
import dna.graph.edges.EdgesAl;
import dna.graph.nodes.NodesAl;

public class UndirectedGraphAlAl extends UndirectedGraph {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public UndirectedGraphAlAl(
			String name,
			long timestamp,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds,
			int nodes, int edges) {
		super(name, timestamp, ds, new NodesAl<UndirectedNode, UndirectedEdge>(
				nodes, (GraphDatastructures) ds), new EdgesAl<UndirectedEdge>(
				edges));
	}

}
