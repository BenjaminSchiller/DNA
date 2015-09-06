package dna.updates.generators.sampling.startNode;

import dna.graph.IGraph;
import dna.graph.nodes.Node;

/**
 * This is a basic selection strategy which simply returns the node with the
 * given ID
 * 
 * @author Benedict Jahn
 * 
 */
public class NodeSelection extends StartNodeSelectionStrategy {

	private int id;

	/**
	 * Creates an NodeSelection instance. It will return the node with the given
	 * ID as start node.
	 * 
	 * @param id
	 *            the ID of the first node
	 */
	public NodeSelection(int id) {
		super();
		this.id = id;
	}

	@Override
	public Node getStartNode(IGraph g) {
		return g.getNode(id);
	}

	@Override
	public int resourceCost(IGraph g) {
		return 1;
	}

}
