package dna.updates.walkingAlgorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.RandomSelection;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class FrontierSampling extends WalkingAlgorithm {

	private HashSet<Node> fullyVisited;

	private LinkedList<Node> walkerPositions;

	int m;

	/**
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param numberOfWalkers
	 * @param parameters
	 */
	public FrontierSampling(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			int numberOfWalkers, Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		if (startNodeStrategy.getClass() != RandomSelection.class) {
			throw new IllegalArgumentException(
					"Frontier sampling does not allow other start node selection strategies than RandomSelection.");
		}

		fullyVisited = new HashSet<Node>(fullGraph.getNodeCount());
		walkerPositions = new LinkedList<Node>();
		m = numberOfWalkers;
	}

	@Override
	protected Node findNextNode() {
		if (walkerPositions.size() < m) {
			Node m1 = fullGraph.getRandomNode();
			addToList(m1);
			return m1;
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

		Node start = startNode.getStartNode();
		walkerPositions.add(start);
		return start;
	}

	private void addToList(Node n) {
		Node tempNode = null;
		for (int i = 0; i < walkerPositions.size(); i++) {
			if ((tempNode = walkerPositions.get(i)) != null) {
				if (getDegreeFromNode(n) > getDegreeFromNode(tempNode)) {
					walkerPositions.add(i, n);
					break;
				}
			} else {
				walkerPositions.add(i, n);
				break;
			}

		}
	}

}
