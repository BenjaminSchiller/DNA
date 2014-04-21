package dna.updates.walkingAlgorithms;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * A sampling algorithm which is based on the behavior of forest fires. Does not
 * allow revisiting.
 * 
 * @author Benedict Jahn
 * 
 */
public class ForestFireNR extends WalkingAlgorithm {

	private HashSet<Node> nodesInQueue;
	private HashSet<Node> fullyVisited;
	private LinkedList<Node> queue;
	private Node currentNode;
	private double probability;

	/**
	 * Creates an instance of the forest fire sampling algorithm
	 * 
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
	 * @param probability
	 *            probability to select a neighbor of the current node. Have to
	 *            be between 0 and 1
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public ForestFireNR(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			double probability, Parameter[] parameters) throws Exception {
		super("FFnr_" + probability, fullGraph, startNodeStrategy,
				onlyVisitedNodesToGraph, costPerBatch, resource, parameters);

		if (probability < 0 || probability > 1) {
			throw new IllegalArgumentException(
					"Probability has to be between 0 and 1.");
		}

		this.probability = probability;
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
	 * Select unvisited neighbors of the current node. Each neighbor is chosen
	 * with the given propability
	 */
	private void selectNeighbors() {
		List<Node> list = getUnvisitedNeighbors(currentNode);
		if (list.isEmpty()) {
			fullyVisited.add(currentNode);
		}
		for (Node n : list) {
			if (!nodesInQueue.contains(n)) {
				if (Rand.rand.nextDouble() <= probability) {
					queue.add(n);
					nodesInQueue.add(n);
				}
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
