package dna.updates.generators.sampling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.generators.sampling.startNode.RandomSelection;
import dna.updates.generators.sampling.startNode.StartNodeSelectionStrategy;
import dna.util.Rand;

/**
 * Implementation of a multiple random walk, which chooses the current walker
 * based on the highest degree.
 * 
 * @author Benedict Jahn
 * 
 */
public class FrontierSampling extends SamplingAlgorithm {

	private HashSet<Node> fullyVisited;

	private LinkedList<Node> walkerPositions;

	int m;
	int initialize;

	/**
	 * Creates an instance of the frontier sampling algorithm
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
	 * @param numberOfWalkers
	 *            the number of nodes which will be considered when choosing the
	 *            next node based on the highest degree
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public FrontierSampling(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, int numberOfWalkers, SamplingStop stop) {
		super("FS_" + numberOfWalkers, fullGraph, startNodeStrategy,
				costPerBatch, resource, stop);

		if (startNodeStrategy.getClass() != RandomSelection.class) {
			throw new IllegalArgumentException(
					"Frontier sampling does not allow other start node selection strategies than RandomSelection.");
		}

		fullyVisited = new HashSet<Node>();
		walkerPositions = new LinkedList<Node>();
		m = numberOfWalkers;
		initialize = 0;
	}

	@Override
	protected Node findNextNode() {
		if (initialize < m) {
			Node m1 = fullGraph.getRandomNode();
			while (walkerPositions.contains(m1)) {
				m1 = fullGraph.getRandomNode();
			}
			addToList(m1);
			initialize++;
			return m1;
		}

		if (walkerPositions.size() < m) {

		}

		Node currentNode = walkerPositions.poll();

		ArrayList<Node> notVisitedNeighbors = getUnvisitedNeighbors(currentNode);
		int neighborCount = notVisitedNeighbors.size();

		if (neighborCount <= 0) {
			fullyVisited.add(currentNode);

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
			addToList(currentNode);

			return findNextNode();
		} else {
			currentNode = notVisitedNeighbors.get(Rand.rand
					.nextInt(neighborCount));

			addToList(currentNode);

			return currentNode;
		}
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {

		Node start = startNode.getStartNode(this.fullGraph);
		walkerPositions.add(start);
		initialize++;
		return start;
	}

	/**
	 * Sorts nodes into the list of walker positions (== nodes). The list is
	 * sorted by the degree of the nodes.
	 * 
	 * @param n
	 *            the node that was visited through the algorithm
	 */
	private void addToList(Node n) {
		int tempDegree = getDegreeFromNode(n);
		for (int i = 0; i < m; i++) {
			if (i >= walkerPositions.size()) {
				walkerPositions.add(i, n);
				break;
			} else {
				Node tempNode = walkerPositions.get(i);
				if (tempDegree > getDegreeFromNode(tempNode)) {
					walkerPositions.add(i, n);
					break;
				}
			}
		}
	}

	@Override
	protected void localReset() {
		fullyVisited = new HashSet<Node>();
		walkerPositions = new LinkedList<Node>();
		initialize = 0;
	}

}
