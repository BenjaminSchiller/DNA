package dna.graph.directed.generators;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

public class RandomDirectedGraph extends DirectedGraphGenerator {

	private int nodes;

	private int edges;

	public RandomDirectedGraph(int nodes, int edges,
			DirectedGraphDatastructures datastructures) {
		this(nodes, edges, datastructures, 0, nodes, edges);
	}

	public RandomDirectedGraph(int nodes, int edges,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super("randomDirectedGraph", new IntParameter("nodes", nodes),
				new IntParameter("edges", edges), datastructures,
				timestampInit, nodesInit, edgesInit);
		this.nodes = nodes;
		this.edges = edges;
	}

	@Override
	public DirectedGraph generate() {
		DirectedGraph graph = this.init();

		for (int i = 0; i < this.nodes; i++) {
			DirectedNode node = this.datastructures.newNodeInstance(i);
			graph.addNode(node);
		}

		while (graph.getEdgeCount() < this.edges) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				DirectedEdge edge = this.datastructures.newEdgeInstance(
						graph.getNode(src), graph.getNode(dst));
				graph.addEdge(edge);
				edge.getSrc().addEdge(edge);
				edge.getDst().addEdge(edge);
			}
		}

		return graph;
	}
}
