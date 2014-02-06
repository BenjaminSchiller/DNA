package dna.updates.walkingAlgorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class RandomWalk extends WalkingAlgorithm {

	private HashSet<Node> fullyVisited;

	private Node currentNode;

	/**
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param parameters
	 */
	public RandomWalk(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		currentNode = null;
		fullyVisited = new HashSet<Node>(fullGraph.getNodeCount());
	}

	@Override
	protected Node findNextNode(Graph fullyGraph, Graph currentGraph) {

		ArrayList<Node> notVisitedNeighbors = getUnvisitedNeighbors(currentNode);
		int neighborCount = notVisitedNeighbors.size();

		if (neighborCount <= 0) {
			fullyVisited.add(currentNode);

			HashSet<Node> alreadyVisitedNodes = getVisitedNodes();
			int notFullyVisitedNodeCount = alreadyVisitedNodes.size()
					- fullyVisited.size();
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

			return findNextNode(fullyGraph, currentGraph);
		}

		currentNode = notVisitedNeighbors.get(Rand.rand.nextInt(neighborCount));

		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		return currentNode;
	}
}
