package dna.updates.samplingAlgorithms.startNodeSelection;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;

/**
 * Basis class for start node selection strategies
 * 
 * @author Benedict Jahn
 * 
 */
public abstract class StartNodeSelectionStrategy {

	/**
	 * Initializes the start node selection strategy.
	 * 
	 * @param g
	 *            the graph from which the strategy shall select the first node.
	 */
	public StartNodeSelectionStrategy() {
	}

	/**
	 * Returns a start node based on the specific node selection strategy
	 */
	public abstract Node getStartNode(Graph g);

	/**
	 * Returns the resource costs for the specific strategy
	 */
	public abstract int resourceCost(Graph g);

	/**
	 * Returns a list of all neighbors of node n
	 * 
	 * @param n
	 *            the node of whom we want to receive the neighbors
	 * @return a list of nodes
	 */
	protected ArrayList<Node> getNeighbors(Node n) {

		ArrayList<Node> neighbors = new ArrayList<Node>();
		Iterable<IElement> iter = getEdgesFromNode(n);

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			neighbors.add(edge.getDifferingNode(n));
		}
		return neighbors;
	}

	/**
	 * Returns the iterator over the outgoing edges of a node (undirected or
	 * directed)
	 * 
	 * @param n
	 *            the node
	 */
	protected Iterable<IElement> getEdgesFromNode(Node n) {
		if (n instanceof DirectedNode) {
			return ((DirectedNode) n).getOutgoingEdges();
		} else {
			return n.getEdges();
		}
	}

	/**
	 * Returns the outdegree of a given node (directed o r undirected)
	 * 
	 * @param n
	 *            the node
	 */
	protected int getDegreeFromNode(Node n) {
		if (n instanceof DirectedNode) {
			return ((DirectedNode) n).getOutDegree();
		} else {
			return ((UndirectedNode) n).getDegree();
		}
	}

}
