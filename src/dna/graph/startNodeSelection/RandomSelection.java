package dna.graph.startNodeSelection;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * Randomly selects a start node out of all nodes.
 * 
 * @author Benedict
 * 
 */
public class RandomSelection extends StartNodeSelectionStrategy {

	/**
	 * 
	 * 
	 * @param g
	 *            The graph from which the node shall be selected
	 */
	public RandomSelection(Graph g) {
		super(g);
	}

	@Override
	public Node getStartNode() {
		int n = g.getNodeCount();
		return g.getNode(Rand.rand.nextInt(n));
	}

	@Override
	public int resourceCost() {
		return 1;
	}

}
