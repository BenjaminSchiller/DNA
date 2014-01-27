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
	 * 
	 * @return success of removal
	 */
	public boolean disconnectFromNodes();

	/**
	 * checks if this edge connects both given nodes
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if this edges connects the two given nodes; false otherwise
	 */
	public boolean isConnectedTo(Node n1, Node n2);

	/**
	 * checks if thus edges connects the given node to another node
	 * 
	 * @param n1
	 * @return true, if this edge connect the giben node; false oterhwise
	 */
	public boolean isConnectedTo(Node n1);
}
