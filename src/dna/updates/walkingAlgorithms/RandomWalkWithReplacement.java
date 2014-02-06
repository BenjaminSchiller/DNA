package dna.updates.walkingAlgorithms;

import java.util.ArrayList;
import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class RandomWalkWithReplacement extends WalkingAlgorithm {

	private Node currentNode;

	/**
	 * 
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param parameters
	 */
	public RandomWalkWithReplacement(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		currentNode = null;
	}

	@Override
	protected Node findNextNode(Graph fullyGraph, Graph currentGraph) {

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

}
