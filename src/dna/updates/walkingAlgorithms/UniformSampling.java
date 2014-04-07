package dna.updates.walkingAlgorithms;

import java.util.LinkedList;

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

	LinkedList<IElement> notVisited;

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

		notVisited = new LinkedList<IElement>(fullGraph.getNodes());
	}

	@Override
	protected Node findNextNode() {
		if(notVisited.isEmpty()){
			noNodeFound();
			return null;
		}
		return (Node) notVisited.remove(Rand.rand.nextInt(notVisited.size()));
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		Node startingNode = startNode.getStartNode();
		notVisited.remove(startingNode);
		return startingNode;
	}

	@Override
	protected void localReset() {
		notVisited = new LinkedList<IElement>(fullGraph.getNodes());
	}

}
