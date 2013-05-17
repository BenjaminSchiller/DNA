package dna.graph.undirected;

import dna.graph.GraphDatastructures;
import dna.graph.GraphImpl;
import dna.graph.edges.Edges;
import dna.graph.nodes.Nodes;

public abstract class UndirectedGraph extends
		GraphImpl<UndirectedNode, UndirectedEdge> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UndirectedGraph(
			String name,
			long timestamp,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds,
			Nodes<UndirectedNode, UndirectedEdge> nodes,
			Edges<UndirectedEdge> edges) {
		super(name, timestamp, (GraphDatastructures) ds, nodes, edges);
	}

}
