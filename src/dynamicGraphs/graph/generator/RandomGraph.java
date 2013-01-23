package dynamicGraphs.graph.generator;

import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.util.Rand;

public class RandomGraph {
	public static Graph generate(int nodes, int edges, boolean undirected) {
		Graph g = new Graph("Random Graph", nodes, 0);
		while (g.getEdges().size() < edges) {
			Edge e = Rand.edge(g);
			g.addEdge(e);
			if (undirected) {
				g.addEdge(e.invert());
			}
		}
		return g;
	}
}
