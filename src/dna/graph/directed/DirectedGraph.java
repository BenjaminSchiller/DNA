package dna.graph.directed;

import dna.graph.GraphImpl;
import dna.graph.edges.Edges;
import dna.graph.nodes.Nodes;

public abstract class DirectedGraph extends
		GraphImpl<DirectedNode, DirectedEdge> {

	public DirectedGraph(String name, long timestamp,
			Nodes<DirectedNode, DirectedEdge> nodes, Edges<DirectedEdge> edges) {
		super(name, timestamp, nodes, edges);
	}

}
