package dynamicGraphs.graph.generator;

import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.util.Rand;

public class RandomGraph extends GraphGenerator {

	public RandomGraph(int nodes, int edges, boolean undirected) {
		super("RandomGraph-" + nodes + "-" + edges + "-" + undirected);
		this.nodes = nodes;
		this.edges = edges;
		this.undirected = undirected;
	}

	private int nodes;

	private int edges;

	private boolean undirected;

	public Graph generate() {
		Graph g = new Graph("Random Graph", this.nodes, 0);
		while (g.getEdges().size() < this.edges) {
			Edge e = Rand.edge(g);
			g.addEdge(e);
			if (this.undirected) {
				g.addEdge(e.invert());
			}
		}
		return g;
	}

}
