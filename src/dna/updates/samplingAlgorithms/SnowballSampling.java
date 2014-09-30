package dna.updates.samplingAlgorithms;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * Implementation of the snowball sampling algorithm. Depending on how you
 * choose the numberOfNeighborsVisited parameter it behaves more like a BFS for
 * higher values or more like a RW for lower values. It is the same algorithm as
 * RDS sampling, but in contrary it does not allow revisiting.
 * 
 * @author Benedict Jahn
 * 
 */
public class SnowballSampling extends SamplingAlgorithm {

	private HashSet<Node> nodesInQueue;
	private HashSet<Node> fullyVisited;
	private LinkedList<Node> queue;
	private Node currentNode;
	private int numberOfNeighborsVisited;

	/**
	 * Creates an instance of the snowball sampling algorithm
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
	 * @param numberOfNeighborsVisited
	 *            count of how many of the neighbors of the current node will be
	 *            queued
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public SnowballSampling(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, int numberOfNeighborsVisited, Parameter[] parameters) {
		super("SS_" + numberOfNeighborsVisited, fullGraph, startNodeStrategy,
				costPerBatch, resource, parameters);

		this.numberOfNeighborsVisited = numberOfNeighborsVisited;
		nodesInQueue = new HashSet<Node>();
		fullyVisited = new HashSet<Node>(fullGraph.getNodeCount());
		queue = new LinkedList<Node>();
		currentNode = null;
	}

	@Override
	protected Node findNextNode() {
		if (queue.isEmpty()) {
			HashSet<Node> alreadyVisitedNodes = getVisitedNodes();
			int notFullyVisitedNodeCount = alreadyVisitedNodes.size()
					- fullyVisited.size();

			if (notFullyVisitedNodeCount <= 0) {

				noNodeFound();
				return null;
			}

			Node[] visitableNodes = new Node[notFullyVisitedNodeCount];

			Iterator<Node> iter = alreadyVisitedNodes.iterator();
			int i = 0;
			while (iter.hasNext()) {
				Node n = iter.next();
				if (!fullyVisited.contains(n)) {
					visitableNodes[i] = n;
					i++;
				}
			}

			currentNode = visitableNodes[Rand.rand
					.nextInt(notFullyVisitedNodeCount)];

			selectNeighbors();
			return findNextNode();
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
				fullyVisited.add(currentNode);
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
		fullyVisited = new HashSet<Node>(fullGraph.getNodeCount());
		queue = new LinkedList<Node>();
		currentNode = null;
	}

}
