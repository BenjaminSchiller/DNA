package dna.graph.directed.generators;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.util.parameters.IntParameter;

public class DirectedClique extends DirectedGraphGenerator {

	private int nodes;

	public DirectedClique(int nodes, DirectedGraphDatastructures datastructures) {
		this(nodes, datastructures, 0, nodes, nodes * (nodes - 1));
	}

	public DirectedClique(int nodes,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super("directedClique", new IntParameter("nodes", nodes),
				datastructures, timestampInit, nodesInit, edgesInit);
		this.nodes = nodes;
	}

	@Override
	public DirectedGraph generate() {
		DirectedGraph graph = this.init();

		for (int i = 0; i < this.nodes; i++) {
			DirectedNode node = this.datastructures.newNodeInstance(i);
			graph.addNode(node);
		}

		for (int i = 0; i < this.nodes; i++) {
			for (int j = 0; j < this.nodes; j++) {
				if (i == j) {
					continue;
				}
				DirectedEdge edge = this.datastructures.newEdgeInstance(
						graph.getNode(i), graph.getNode(j));
				graph.addEdge(edge);
				edge.getSrc().addEdge(edge);
				edge.getDst().addEdge(edge);
			}
		}

		return graph;
	}

}
