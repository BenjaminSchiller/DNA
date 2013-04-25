package dna.graph.directed;

import dna.graph.Graph;

public abstract class DirectedGraph extends Graph<DirectedNode, DirectedEdge> {

	public DirectedGraph(String name, long timestamp) {
		super(name, timestamp);
	}

}
