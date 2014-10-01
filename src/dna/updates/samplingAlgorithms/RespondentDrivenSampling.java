package dna.updates.samplingAlgorithms;

import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;

/**
 * Implementation of the respondent driven sampling algorithm. Depending on how
 * you choose the numberOfNeighborsVisited parameter it behaves more like a BFS
 * for higher values or more like a RW for lower values. It is the same
 * algorithm as snowball sampling, but in contrary it allows revisiting of
 * nodes.
 * 
 * @author Benedict Jahn
 * 
 */
public class RespondentDrivenSampling extends SamplingAlgorithm {

	private LinkedList<Node> queue;
	private Node currentNode;
	private int numberOfNeighborsVisited;

	/**
	 * Creates an instance of the respondent driven sampling algorithm
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
	public RespondentDrivenSampling(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, int numberOfNeighborsVisited) {
		super("RDS_" + numberOfNeighborsVisited, fullGraph, startNodeStrategy,
				costPerBatch, resource);

		this.numberOfNeighborsVisited = numberOfNeighborsVisited;
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
		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		selectNeighbors();
		return currentNode;
	}

	/**
	 * Randomly selects the chosen amount of all neighbors from the current node
	 * and adds them to the queue
	 */
	private void selectNeighbors() {
		List<Node> list = getAllNeighbors(currentNode);
		for (int i = 0; i < numberOfNeighborsVisited; i++) {
			if (list.size() == 0) {
				break;
			}
			queue.add(list.remove(Rand.rand.nextInt(list.size())));
		}
	}

	@Override
	protected void localReset() {
		queue = new LinkedList<Node>();
		currentNode = null;
	}

}
