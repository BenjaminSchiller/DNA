package dna.graph.startNodeSelection;

import dna.graph.nodes.Node;

/**
 * Implements a start node selection strategy.
 * 
 * @author Benedict
 * 
 */
public interface StartNodeSelectionStrategy {

	/**
	 * Returns a start node based on the specific node selection strategy.
	 */
	public Node getStartNode();

	/**
	 * Returns the resource costs for this strategy
	 */
	public int resourceCost();

}
