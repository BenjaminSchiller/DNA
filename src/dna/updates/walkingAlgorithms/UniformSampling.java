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
	 * Creates an instance of the uniform sampling algorithm
	 * 
	 * @param fullGraph
	 *            the graph the algorithm shall walk on
	 * @param startNodeStrat
	 *            the strategy how the algorithm will select the first node
	 * @param onlyVisitedNodesToGraph
	 *            if set to true the generator will only put visited nodes in
	 *            the batch
	 * @param costPerBatch
	 *            how many steps the algorithm shall perform for one batch
	 * @param ressouce
	 *            the maximum count of steps the algorithm shall perform, if
	 *            initialized with 0 or below the algorithm will walk until the
	 *            graph is fully visited
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public UniformSampling(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super("US", fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		notVisited = new LinkedList<IElement>(fullGraph.getNodes());
	}

	@Override
	protected Node findNextNode() {
		if (notVisited.isEmpty()) {
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
