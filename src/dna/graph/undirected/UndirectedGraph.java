package dna.graph.undirected;

import dna.graph.Graph;

public abstract class UndirectedGraph extends
		Graph<UndirectedNode, UndirectedEdge> {

	public UndirectedGraph(String name, long timestamp) {
		super(name, timestamp);
	}
}
