package dna.updates.samplingAlgorithms;

import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;

/**
 * A sampling algorithm which is based on the behavior of forest fires.
 * 
 * @author Benedict Jahn
 * 
 */
public class ForestFire extends SamplingAlgorithm {

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
	public ForestFire(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, double probability) throws Exception {
		super("FF_" + probability, fullGraph, startNodeStrategy, costPerBatch,
				resource);

		if (probability < 0 || probability > 1) {
			throw new IllegalArgumentException(
					"Probability has to be between 0 and 1.");
		}

		this.probability = probability;
		queue = new LinkedList<Node>();
		currentNode = null;

	}

	@Override
	protected Node findNextNode() {
		if (queue.isEmpty()) {
			currentNode = fullGraph.getRandomNode();
			selectNeighbors();
			return currentNode;
		}
		currentNode = queue.poll();
		selectNeighbors();
		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode(this.fullGraph);
		selectNeighbors();
		return currentNode;
	}

	/**
	 * Select neighbors of the current node. Each neighbor is chosen with the
	 * given probability.
	 */
	private void selectNeighbors() {
		List<Node> list = getAllNeighbors(currentNode);
		for (Node n : list) {
			if (Rand.rand.nextDouble() <= probability) {
				queue.add(n);
			}
		}
	}

	@Override
	protected void localReset() {
		queue = new LinkedList<Node>();
		currentNode = null;
	}

}
