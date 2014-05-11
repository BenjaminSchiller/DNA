package dna.updates.samplingAlgorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * A sampling algorithm based on deep first search, but with randomized node
 * lists
 * 
 * @author Benedict Jahn
 * 
 */
public class DFS_random extends SamplingAlgorithm {

	private HashSet<Node> nodesInQueue;
	private LinkedList<Node> queue;
	private Node currentNode;

	/**
	 * Creates an instance of the depth first sampling algorithm with randomized
	 * node lists
	 * 
	 * @param fullGraph
	 *            the graph the algorithm shall walk on
	 * @param startNodeStrat
	 *            the strategy how the algorithm will select the first node
	 * @param costPerBatch
	 *            how many steps the algorithm shall perform for one batch
	 * @param ressouce
	 *            the maximum count of steps the algorithm shall perform, if
	 *            initialized with 0 or below the algorithm will walk until the
	 *            graph is fully visited
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public DFS_random(Graph fullGraph, StartNodeSelectionStrategy startNodeStrategy,
			int costPerBatch, int resource, Parameter[] parameters) {
		super("DFS_random", fullGraph, startNodeStrategy, costPerBatch,
				resource, parameters);

		queue = new LinkedList<Node>();
		nodesInQueue = new HashSet<Node>();
		currentNode = null;
	}

	@Override
	protected Node findNextNode() {
		if (queue.isEmpty()) {
			noNodeFound();
			return null;
		}

		currentNode = queue.removeLast();
		ArrayList<Node> neighborsList = getUnvisitedNeighborsRandomized(currentNode);
		for (Node n : neighborsList) {
			if (!nodesInQueue.contains(n)) {
				queue.add(n);
				nodesInQueue.add(n);
			}
		}
		nodesInQueue.remove(currentNode);
		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		nodesInQueue.add(currentNode);
		ArrayList<Node> neighborsList = getUnvisitedNeighborsRandomized(currentNode);
		for (Node n : neighborsList) {
			if (!nodesInQueue.contains(n)) {
				queue.add(n);
				nodesInQueue.add(n);
			}
		}
		return currentNode;
	}

	@Override
	protected void localReset() {
		queue = new LinkedList<Node>();
		nodesInQueue = new HashSet<Node>();
		currentNode = null;
	}

	/**
	 * Returns a randomly ordered list of unvisited neighbors of node n
	 * 
	 * @param n
	 *            the node of whom we want to receive the unvisited neighbors
	 * @return a list of nodes
	 */
	private ArrayList<Node> getUnvisitedNeighborsRandomized(Node n) {

		ArrayList<Node> neighbors = new ArrayList<Node>();
		Iterable<IElement> iter = getEdgesFromNode(n);

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			Node neighbor = edge.getDifferingNode(n);
			if (!getVisitedNodes().contains(neighbor)) {
				neighbors.add(neighbor);
			}
		}

		// Now we randomly shuffle the array list with the neighbors
		ArrayList<Node> result = new ArrayList<Node>();
		int size = neighbors.size();
		for (int i = 0; i < size; i++) {
			Node tempNode = neighbors
					.remove(Rand.rand.nextInt(neighbors.size()));
			result.add(tempNode);
		}
		return result;
	}

}
