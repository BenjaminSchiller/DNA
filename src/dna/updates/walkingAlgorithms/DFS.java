package dna.updates.walkingAlgorithms;

import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class DFS extends WalkingAlgorithm {

	private Node currentNode;
	private LinkedList<Node> queue;

	/**
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param parameters
	 */
	public DFS(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		queue = new LinkedList<Node>();
		currentNode = null;
	}

	@Override
	protected Node findNextNode(Graph fullyGraph, Graph currentGraph) {
		currentNode = queue.removeLast();
		queue.addAll(getUnvisitedNeighbors(currentNode));
		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		queue.addAll(getUnvisitedNeighbors(currentNode));
		return currentNode;
	}

}
