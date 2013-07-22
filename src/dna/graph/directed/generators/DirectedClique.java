package dna.graph.directed.generators;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.util.parameters.IntParameter;

/**
 * 
 * graph generator for a directed clique, i.e., a directed graph that contains
 * all possible (n * (n-1)) edges
 * 
 * @author benni
 * 
 */
public class DirectedClique extends DirectedGraphGenerator {

	private int nodes;

	/**
	 * 
	 * @param nodes
	 *            number of nodes / size of the clique
	 * @param datastructures
	 *            datastructures
	 */
	public DirectedClique(int nodes, DirectedGraphDatastructures datastructures) {
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
	public DirectedClique(int nodes,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super("directedClique", new IntParameter("nodes", nodes),
				datastructures, timestampInit, nodesInit, edgesInit);
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
			for (int j = 0; j < this.nodes; j++) {
				if (i == j) {
					continue;
				}
				DirectedEdge edge = this.datastructures.newEdgeInstance(
						graph.getNode(i), graph.getNode(j));
				graph.addEdge(edge);
				edge.getSrc().addEdge(edge);
				edge.getDst().addEdge(edge);
			}
		}

		return graph;
	}

}
