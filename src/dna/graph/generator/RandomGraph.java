package dna.graph.generator;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.util.Rand;

/**
 * 
 * Implements a random graph generator. Given a number of nodes and edges, the
 * specified umber of edges is added to graph by choosing source and destination
 * uniformly at random.
 * 
 * @author benni
 * 
 */
public class RandomGraph extends GraphGenerator {

	/**
	 * 
	 * @param nodes
	 *            number of nodes
	 * @param edges
	 *            number of edges
	 * @param undirected
	 *            if true, an undirected graph is generated where for each edge
	 *            (a->b), the inverse (b->a) is added as well.
	 */
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
