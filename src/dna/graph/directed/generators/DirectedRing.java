package dna.graph.directed.generators;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.util.parameters.IntParameter;

/**
 * 
 * graph generator for a directed ring topology, i.e., 0 -> 1 -> 2 -> 3 -> 0
 * 
 * @author benni
 * 
 */
public class DirectedRing extends DirectedGraphGenerator {

	private int nodes;

	/**
	 * 
	 * @param nodes
	 *            number of nodes in the ring
	 * @param datastructures
	 *            datastructures
	 */
	public DirectedRing(int nodes, DirectedGraphDatastructures datastructures) {
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
	public DirectedRing(int nodes, DirectedGraphDatastructures datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super("directedRing", new IntParameter("nodes", nodes), datastructures,
				timestampInit, nodesInit, edgesInit);
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
			DirectedEdge edge = this.datastructures.newEdgeInstance(
					graph.getNode(i), graph.getNode((i + 1) % this.nodes));
			graph.addEdge(edge);
			edge.getSrc().addEdge(edge);
			edge.getDst().addEdge(edge);
		}

		return graph;
	}

}
