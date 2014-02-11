package dna.updates.walkingAlgorithms;

import java.util.ArrayList;
import java.util.Collections;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.updates.walkingAlgorithms.SortableNode.SortType;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class MaximumObservedDegree extends WalkingAlgorithm {

	ArrayList<SortableNode> greyZone;

	/**
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param parameters
	 */
	public MaximumObservedDegree(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		greyZone = new ArrayList<SortableNode>(fullGraph.getNodeCount());
	}

	@Override
	protected Node findNextNode() {
		Collections.sort(greyZone);
		Node newNode = greyZone.remove(0).getNode();

		ArrayList<Node> neighbors = getUnvisitedNeighbors(newNode);
		for (Node n : neighbors) {
			greyZone.add(new SortableNode(n, this,
					SortType.SORT_BY_VISITED_NEIGHBORS));
		}

		return newNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		Node firstNode = startNode.getStartNode();
		ArrayList<Node> neighbors = getUnvisitedNeighbors(firstNode);
		for (Node n : neighbors) {
			greyZone.add(new SortableNode(n, this,
					SortType.SORT_BY_VISITED_NEIGHBORS));
		}
		return firstNode;
	}

}
