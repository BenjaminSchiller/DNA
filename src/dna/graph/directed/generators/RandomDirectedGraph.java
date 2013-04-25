package dna.graph.directed.generators;

import dna.graph.GraphDatastructures;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

public class RandomDirectedGraph extends DirectedGraphGenerator {

	private int nodes;

	private int edges;

	public RandomDirectedGraph(int nodes, int edges,
			GraphDatastructures<DirectedEdge> datastructures) {
		super("randomDirectedGraph", new IntParameter("nodeds", nodes),
				new IntParameter("edges", edges), datastructures);
		this.nodes = nodes;
		this.edges = edges;
	}

	@Override
	public DirectedGraph generate() {
		System.out.println(this.datastructures.toString());

		DirectedGraph graph = this.getGraphInstance(this.getName(), 0,
				this.nodes, this.edges);
		System.out.println("==> " + graph);
		for (int i = 0; i < this.nodes; i++) {
			DirectedNode node = this.getNodeInstance(i);
			graph.addNode(node);
		}
		System.out.println("==> " + graph);
		while (graph.getEdgeCount() < this.edges) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				DirectedEdge edge = this.getEdgeInstance(graph.getNode(src),
						graph.getNode(dst));
				graph.addEdge(edge);
			}
		}
		System.out.println("==> " + graph);

		return graph;
	}
}
