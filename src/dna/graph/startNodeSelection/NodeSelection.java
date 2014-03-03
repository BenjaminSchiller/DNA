package dna.graph.startNodeSelection;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;

/**
 * This is a basic selection strategy which simply returns the node with the
 * given ID
 * 
 * @author Benedict
 * 
 */
public class NodeSelection extends StartNodeSelectionStrategy {

	private int id;

	/**
	 * Creates an NodeSelection instance. It will return the node with the given
	 * ID as start node.
	 * 
	 * @param g
	 *            the graph from which the node shall be selected
	 * @param id
	 *            the ID of the first node
	 */
	public NodeSelection(Graph g, int id) {
		super(g);
		this.id = id;
	}

	@Override
	public Node getStartNode() {
		return g.getNode(id);
	}

	@Override
	public int resourceCost() {
		return 1;
	}

}
