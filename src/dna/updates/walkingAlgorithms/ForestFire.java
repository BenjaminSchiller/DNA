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
public class ForestFire extends WalkingAlgorithm {

	private LinkedList<Node> queue;
	private Node currentNode;
	private double propability;

	/**
	 * 
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param propability
	 *            propability to select a neighbor of the current node. Have to
	 *            be between 0 and 1
	 * @param parameters
	 * @throws Exception
	 */
	public ForestFire(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			double propability, Parameter[] parameters) throws Exception {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		if (propability < 0 || propability > 1) {
			throw new IllegalArgumentException("Propability has to be between 0 and 1.");
		}

		this.propability = propability;
		queue = new LinkedList<Node>();
		currentNode = null;

	}

	@Override
	protected Node findNextNode() {
		if(queue.isEmpty()){
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
		currentNode = startNode.getStartNode();
		selectNeighbors();
		return currentNode;
	}

	/**
	 * 
	 */
	private void selectNeighbors() {
		List<Node> list = getAllNeighbors(currentNode);
		for (Node n : list) {
			if (Rand.rand.nextDouble() <= propability) {
				queue.add(n);
			}
		}
	}

}
