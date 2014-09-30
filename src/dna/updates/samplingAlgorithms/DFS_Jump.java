package dna.updates.samplingAlgorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.parameters.Parameter;

/**
 * A sampling algorithm based on depth first search
 * 
 * @author Benedict Jahn
 * 
 */
public class DFS_Jump extends SamplingAlgorithm {

	private HashSet<Node> nodesInQueue;
	private LinkedList<Node> queue;
	private Node currentNode;

	/**
	 * Creates an instance of the depth first sampling algorithm
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
	public DFS_Jump(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, Parameter[] parameters) {
		super("DFS_Jump", fullGraph, startNodeStrategy, costPerBatch, resource,
				parameters);

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
		ArrayList<Node> neighborsList = getUnvisitedNeighbors(currentNode);
		if (neighborsList.size() == 0) {
			System.out.println("jump");
		}
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

		ArrayList<Node> neighborsList = getUnvisitedNeighbors(currentNode);
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

}
