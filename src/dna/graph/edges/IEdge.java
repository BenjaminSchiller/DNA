package dna.graph.edges;

import dna.graph.IElement;
import dna.graph.nodes.Node;

/**
 * Interface for all types of edges
 * 
 * @author Nico
 * 
 */
public interface IEdge extends IElement {
	/**
	 * Call this method to get the opposite end of an edge
	 * 
	 * @param n
	 *            "Entry" node of this edge
	 * @return "Exit" node of this edge
	 */
	public Node getDifferingNode(Node n);

	/**
	 * Register this edge at both aligning nodes
	 * 
	 * @return success of addition
	 */
	public boolean connectToNodes();
	
	/**
	 * Remove this edge from both aligning nodes
	 * @return success of removal
	 */
	public boolean disconnectFromNodes();
}
