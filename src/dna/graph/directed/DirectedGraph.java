package dna.graph.directed;

import dna.graph.GraphDatastructures;
import dna.graph.GraphImpl;
import dna.graph.edges.Edges;
import dna.graph.nodes.Nodes;

public abstract class DirectedGraph extends
		GraphImpl<DirectedNode, DirectedEdge> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DirectedGraph(String name, long timestamp,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds,
			Nodes<DirectedNode, DirectedEdge> nodes, Edges<DirectedEdge> edges) {
		super(name, timestamp, (GraphDatastructures) ds, nodes, edges);
	}

}
