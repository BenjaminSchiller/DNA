package dna.updates.samplingAlgorithms;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * Implementation of a random walk sampling algorithm. It randomly chooses the
 * next node out of all neighbors of the current node. It therefore allows
 * revisiting of nodes.
 * 
 * @author Benedict Jahn
 * 
 */
public class RandomWalk extends SamplingAlgorithm {

	private Node currentNode;

	/**
	 * Creates an instance of the random walk sampling algorithm with revisiting
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
	public RandomWalk(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, Parameter[] parameters) {
		super("RW", fullGraph, startNodeStrategy, costPerBatch, resource,
				parameters);

		currentNode = null;
	}

	@Override
	protected Node findNextNode() {

		ArrayList<Node> neighbors = getAllNeighbors(currentNode);
		int neighborCount = neighbors.size();

		currentNode = neighbors.get(Rand.rand.nextInt(neighborCount));

		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		return currentNode;
	}

	@Override
	protected void localReset() {
		currentNode = null;
	}

}
