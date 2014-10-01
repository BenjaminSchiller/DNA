package dna.updates.generators.sampling.startNode;

import dna.graph.Graph;
import dna.graph.nodes.Node;

/**
 * Randomly selects a start node out of all nodes.
 * 
 * @author Benedict Jahn
 * 
 */
public class RandomSelection extends StartNodeSelectionStrategy {

	/**
	 * Creates an instance of the random selection start node selection strategy
	 * 
	 */
	public RandomSelection() {
		super();
	}

	@Override
	public Node getStartNode(Graph g) {
		return g.getRandomNode();
	}

	@Override
	public int resourceCost(Graph g) {
		return 1;
	}

}
