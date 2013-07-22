package dna.graph.undirected.generators;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedGraphDatastructures;
import dna.graph.undirected.UndirectedNode;
import dna.util.Rand;
import dna.util.parameters.IntParameter;

/**
 * 
 * graph generator for a random undirected graph. given a number of nodes and
 * edges, the edges are chosen uniformly at random from the number of all
 * possible edges.
 * 
 * @author benni
 * 
 */
public class RandomUndirectedGraph extends UndirectedGraphGenerator {

	private int nodes;

	private int edges;

	/**
	 * 
	 * @param nodes
	 *            number of ndoes to generate
	 * @param edges
	 *            number of edges to randomly assign between the nodes
	 * @param datastructures
	 *            datastructures
	 */
	public RandomUndirectedGraph(int nodes, int edges,
			UndirectedGraphDatastructures datastructures) {
		this(nodes, edges, datastructures, 0, nodes, edges);
	}

	/**
	 * 
	 * @param nodes
	 *            number of ndoes to generate
	 * @param edges
	 *            number of edges to randomly assign between the nodes
	 * @param datastructures
	 *            datastructures
	 * @param timestampInit
	 *            timestamp to assign to the generated graph
	 * @param nodesInit
	 *            number of nodes to initialize the datastructures with
	 * @param edgesInit
	 *            number of edges to initialize the datastructures with
	 */
	public RandomUndirectedGraph(int nodes, int edges,
			UndirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
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
