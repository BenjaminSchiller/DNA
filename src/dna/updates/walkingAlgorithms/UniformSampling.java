package dna.updates.walkingAlgorithms;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.Rand;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class UniformSampling extends WalkingAlgorithm {

	ArrayList<IElement> notVisited;

	/**
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param parameters
	 */
	public UniformSampling(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		notVisited = Lists.newArrayList(fullGraph.getNodes());
	}

	@Override
	protected Node findNextNode(Graph fullyGraph, Graph currentGraph) {

		return (Node) notVisited.remove(Rand.rand.nextInt(notVisited.size()));
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		Node startingNode = startNode.getStartNode();
		notVisited.remove(startingNode);
		return startingNode;
	}

}
