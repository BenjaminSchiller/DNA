package dna.updates.walkingAlgorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * Implementation of the snowball sampling algorithm. Depending on how you
 * choose the numberOfNeighborsVisited parameter it behaves more like a BFS for
 * higher values or more like a DFS for lower values. It is the same algorithm
 * as RDS sampling, but in contrary it does not allow revisiting.
 * 
 * @author Benedict Jahn
 * 
 */
public class SnowballSampling extends WalkingAlgorithm {

	private HashSet<Node> nodesInQueue;
	private LinkedList<Node> queue;
	private Node currentNode;
	private int numberOfNeighborsVisited;

	/**
	 * Creates an instance of the snowball sampling algorithm
	 * 
	 * @param name
	 *            the name of this instance
	 * @param fullGraph
	 *            the graph the algorithm shall walk on
	 * @param startNodeStrat
	 *            the strategy how the algorithm will select the first node
	 * @param onlyVisitedNodesToGraph
	 *            if set to true the generator will only put visited nodes in
	 *            the batch
	 * @param costPerBatch
	 *            how many steps the algorithm shall perform for one batch
	 * @param ressouce
	 *            the maximum count of steps the algorithm shall perform, if
	 *            initialized with 0 or below the algorithm will walk until the
	 *            graph is fully visited
	 * @param numberOfNeighborsVisited
	 *            count of how many of the neighbors of the current node will be
	 *            queued
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public SnowballSampling(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			int numberOfNeighborsVisited, Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		this.numberOfNeighborsVisited = numberOfNeighborsVisited;
		nodesInQueue = new HashSet<Node>();
		queue = new LinkedList<Node>();
		currentNode = null;
	}

	@Override
	protected Node findNextNode() {
		if (queue.isEmpty()) {
			noNodeFound();
			return null;
		}
		currentNode = queue.poll();
		selectNeighbors();
		nodesInQueue.remove(currentNode);
		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		nodesInQueue.add(currentNode);
		selectNeighbors();
		return currentNode;
	}

	/**
	 * Randomly selects the chosen amount of unvisited neighbors from the
	 * current node and adds them to the queue
	 */
	private void selectNeighbors() {
		List<Node> list = getUnvisitedNeighbors(currentNode);
		for (int i = 0; i < numberOfNeighborsVisited; i++) {
			if (list.isEmpty()) {
				break;
			}
			Node n = list.remove(Rand.rand.nextInt(list.size()));
			if (!nodesInQueue.contains(n)) {
				queue.add(n);
				nodesInQueue.add(n);
			}
		}
	}

	@Override
	protected void localReset() {
		nodesInQueue = new HashSet<Node>();
		queue = new LinkedList<Node>();
		currentNode = null;
	}

}
