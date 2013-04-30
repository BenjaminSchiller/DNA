package dna.graph.undirected;

import dna.graph.GraphImpl;
import dna.graph.edges.Edges;
import dna.graph.nodes.Nodes;

public abstract class UndirectedGraph extends
		GraphImpl<UndirectedNode, UndirectedEdge> {

	public UndirectedGraph(String name, long timestamp,
			Nodes<UndirectedNode, UndirectedEdge> nodes,
			Edges<UndirectedEdge> edges) {
		super(name, timestamp, nodes, edges);
	}

}
