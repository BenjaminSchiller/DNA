package dna.graph.undirected.generators;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedGraphDatastructures;
import dna.graph.undirected.UndirectedNode;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

public class RandomUndirectedGraph extends UndirectedGraphGenerator {

	private int nodes;

	private int edges;

	public RandomUndirectedGraph(int nodesInit, int nodes, int edgesInit,
			int edges, long timestampInit,
			UndirectedGraphDatastructures datastructures) {
		super("randomUndirectedGraph", new IntParameter("nodes", nodes),
				new IntParameter("edges", edges), datastructures,
				timestampInit, nodesInit, edgesInit);
		this.nodes = nodes;
		this.edges = edges;
	}

	@Override
	public UndirectedGraph generate() {
		UndirectedGraph graph = this.init();

		for (int i = 0; i < this.nodes; i++) {
			UndirectedNode node = this.datastructures.newNodeInstance(i);
			graph.addNode(node);
		}

		while (graph.getEdgeCount() < this.edges) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				UndirectedEdge edge = this.datastructures.newEdgeInstance(
						graph.getNode(src), graph.getNode(dst));
				graph.addEdge(edge);
				edge.getNode1().addEdge(edge);
				edge.getNode2().addEdge(edge);
			}
		}

		return graph;
	}

}
