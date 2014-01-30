package dna.util;

import java.util.ArrayList;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;

/**
 * @author Benedict
 * 
 */
public class NodeUtils {

	/**
	 * 
	 * @param n
	 * @return
	 */
	public static ArrayList<Node> getNeighbors(Node n) {

		ArrayList<Node> neighbors = new ArrayList<>();
		Iterable<IElement> iter = n.getEdges();

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			neighbors.add(edge.getDifferingNode(n));
		}
		return neighbors;
	}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public static ArrayList<UndirectedNode> getNeighbors(UndirectedNode n) {

		return NodeUtils.getNeighbors(n);
	}

	public static ArrayList<DirectedNode> getNeighbors(DirectedNode n) {

		ArrayList<DirectedNode> neighbors = new ArrayList<>();
		Iterable<IElement> iter = n.getOutgoingEdges();

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			neighbors.add((DirectedNode) edge.getDifferingNode(n));
		}

		return neighbors;
	}

}
