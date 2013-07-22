package dna.graph.undirected.generators;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedGraphDatastructures;
import dna.graph.undirected.UndirectedNode;
import dna.util.parameters.IntParameter;

/**
 * 
 * graph generator for a directed clique, i.e., a directed graph that contains
 * all possible (n * (n-1)) edges
 * 
 * @author benni
 * 
 */
public class UndirectedClique extends UndirectedGraphGenerator {

	private int nodes;

	/**
	 * 
	 * @param nodes
	 *            number of nodes / size of the clique
	 * @param datastructures
	 *            datastructures
	 */
	public UndirectedClique(int nodes,
			UndirectedGraphDatastructures datastructures) {
		this(nodes, datastructures, 0, nodes, nodes * (nodes - 1));
	}

	/**
	 * 
	 * @param nodes
	 *            number of nodes / size of the clique
	 * @param datastructures
	 *            datastructures
	 * @param timestampInit
	 *            timestamp to assign to the generated graph
	 * @param nodesInit
	 *            number of nodes to initialize the datastructures with
	 * @param edgesInit
	 *            number of edges to initialize the datastructures with
	 */
	public UndirectedClique(int nodes,
			UndirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super("undirectedClique", new IntParameter("nodes", nodes),
				datastructures, timestampInit, nodesInit, edgesInit);
		this.nodes = nodes;
	}

	@Override
	public UndirectedGraph generate() {
		UndirectedGraph graph = this.init();

		for (int i = 0; i < this.nodes; i++) {
			UndirectedNode node = this.datastructures.newNodeInstance(i);
			graph.addNode(node);
		}

		for (int i = 0; i < this.nodes; i++) {
			for (int j = i + 1; j < this.nodes; j++) {
				if (i == j) {
					continue;
				}
				UndirectedEdge edge = this.datastructures.newEdgeInstance(
						graph.getNode(i), graph.getNode(j));
				graph.addEdge(edge);
				edge.getNode1().addEdge(edge);
				edge.getNode2().addEdge(edge);
			}
		}

		return graph;
	}

}
