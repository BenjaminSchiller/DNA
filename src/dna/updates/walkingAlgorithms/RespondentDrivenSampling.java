package dna.updates.walkingAlgorithms;

import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class RespondentDrivenSampling extends WalkingAlgorithm {

	private LinkedList<Node> queue;
	private Node currentNode;
	private int numberOfNeighborsVisited;

	/**
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param parameters
	 */
	public RespondentDrivenSampling(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			int numberOfNeighborsVisited, Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		this.numberOfNeighborsVisited = numberOfNeighborsVisited;
		queue = new LinkedList<Node>();
		currentNode = null;
	}

	@Override
	protected Node findNextNode(Graph fullyGraph, Graph currentGraph) {
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
	 * 
	 */
	private void selectNeighbors() {
		List<Node> list = getAllNeighbors(currentNode);
		for (int i = 0; i < numberOfNeighborsVisited; i++) {
			queue.add(list.get(Rand.rand.nextInt(list.size())));
		}
	}

}
