package dna.graph.undirected.generators;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

public class RandomUndirectedGraph extends UndirectedGraphGenerator {

	private int nodes;

	private int edges;

	public RandomUndirectedGraph(int nodes, int edges,
			GraphDatastructures<UndirectedEdge> datastructures) {
		super("randomUndirectedGraph", new IntParameter("nodes", nodes),
				new IntParameter("edges", edges), datastructures);
		this.nodes = nodes;
		this.edges = edges;
	}

	@Override
	public Graph<? extends Node<UndirectedEdge>, UndirectedEdge> generate() {
		System.out.println(this.datastructures.toString());

		UndirectedGraph graph = this.getGraphInstance(this.getName(), 0,
				this.nodes, this.edges);
		System.out.println("==> " + graph);
		for (int i = 0; i < this.nodes; i++) {
			UndirectedNode node = this.getNodeInstance(i);
			graph.addNode(node);
		}
		System.out.println("==> " + graph);
		while (graph.getEdgeCount() < this.edges) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				UndirectedEdge edge = this.getEdgeInstance(graph.getNode(src),
						graph.getNode(dst));
				graph.addEdge(edge);
			}
		}
		System.out.println("==> " + graph);

		return graph;
	}

}
