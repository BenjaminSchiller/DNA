package dna.graph.directed.generators;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.util.parameters.IntParameter;

public class DirectedRing extends DirectedGraphGenerator {

	private int nodes;

	public DirectedRing(int nodes, DirectedGraphDatastructures datastructures) {
		this(nodes, datastructures, 0, nodes, nodes);
	}

	public DirectedRing(int nodes, DirectedGraphDatastructures datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super("directedRing", new IntParameter("nodes", nodes), datastructures,
				timestampInit, nodesInit, edgesInit);
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
			DirectedEdge edge = this.datastructures.newEdgeInstance(
					graph.getNode(i), graph.getNode((i + 1) % this.nodes));
			graph.addEdge(edge);
			edge.getSrc().addEdge(edge);
			edge.getDst().addEdge(edge);
		}

		return graph;
	}

}
