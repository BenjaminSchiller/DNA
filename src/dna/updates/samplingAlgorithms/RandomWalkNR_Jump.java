package dna.updates.samplingAlgorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * Implementation of a random walk sampling algorithm. It randomly chooses the
 * next node out of the unvisited neighbors of the current node. Therefore it
 * does not allow revisiting.
 * 
 * @author Benedict Jahn
 * 
 */
public class RandomWalkNR_Jump extends SamplingAlgorithm {

	private HashSet<Node> fullyVisited;

	private Node currentNode;

	/**
	 * Creates an instance of the random walk sampling algorithm without
	 * revisiting
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
	public RandomWalkNR_Jump(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, Parameter[] parameters) {
		super("RWnr_Jump", fullGraph, startNodeStrategy, costPerBatch, resource,
				parameters);

		currentNode = null;
		fullyVisited = new HashSet<Node>(fullGraph.getNodeCount());
	}

	@Override
	protected Node findNextNode() {

		ArrayList<Node> notVisitedNeighbors = getUnvisitedNeighbors(currentNode);
		int neighborCount = notVisitedNeighbors.size();

		if (neighborCount <= 0) {
			fullyVisited.add(currentNode);

			System.out.println("jump");

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

			return findNextNode();
		} else {

			currentNode = notVisitedNeighbors.get(Rand.rand
					.nextInt(neighborCount));

			return currentNode;
		}
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		return currentNode;
	}

	@Override
	protected void localReset() {
		currentNode = null;
		fullyVisited = new HashSet<Node>(fullGraph.getNodeCount());
	}
}
