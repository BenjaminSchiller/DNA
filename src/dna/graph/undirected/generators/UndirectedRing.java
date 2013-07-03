package dna.graph.undirected.generators;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedGraphDatastructures;
import dna.graph.undirected.UndirectedNode;
import dna.util.parameters.IntParameter;

/**
 * 
 * graph generator for a directed ring topology, i.e., 0 -> 1 -> 2 -> 3 -> 0
 * 
 * @author benni
 * 
 */
public class UndirectedRing extends UndirectedGraphGenerator {

	private int nodes;

	/**
	 * 
	 * @param nodes
	 *            number of nodes in the ring
	 * @param datastructures
	 *            datastructures
	 */
	public UndirectedRing(int nodes,
			UndirectedGraphDatastructures datastructures) {
		this(nodes, datastructures, 0, nodes, nodes);
	}

	/**
	 * 
	 * @param nodes
	 *            number of nodes in the ring
	 * @param datastructures
	 *            datastructures
	 * @param timestampInit
	 *            timestamp to assign to the generated graph
	 * @param nodesInit
	 *            number of nodes to initialize the datastructures with
	 * @param edgesInit
	 *            number of edges to initialize the datastructures with
	 */
	public UndirectedRing(int nodes,
			UndirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super("undirectedRing", new IntParameter("nodes", nodes),
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
			UndirectedEdge edge = this.datastructures.newEdgeInstance(
					graph.getNode(i), graph.getNode((i + 1) % this.nodes));
			graph.addEdge(edge);
			edge.getNode1().addEdge(edge);
			edge.getNode2().addEdge(edge);
		}

		return graph;
	}

}
