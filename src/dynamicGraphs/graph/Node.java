package dynamicGraphs.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author benni
 * 
 */
// TODO add list / sets of edges
public class Node {
	/**
	 * 
	 * @param index
	 *            index of the node as it is referenced in the graph and in
	 *            string representations
	 */
	public Node(int index) {
		this.index = index;
		this.in = new HashSet<Node>();
		this.out = new HashSet<Node>();
		this.neighbors = new HashSet<Node>();
	}

	public String toString() {
		return this.index + " (" + this.in.size() + "/" + this.out.size() + ")";
	}

	private int index;

	/**
	 * 
	 * @return index of the node
	 */
	public int getIndex() {
		return this.index;
	}

	private Set<Node> in;

	/**
	 * 
	 * @return set of nodes that have an edge pointing to this node (in-list)
	 */
	public Set<Node> getIn() {
		return this.in;
	}

	/**
	 * 
	 * @param v
	 * @return true if the given node is in the in-list
	 */
	public boolean hasIn(Node v) {
		return this.in.contains(v);
	}

	/**
	 * adds the given node to the in-list
	 * 
	 * @param v
	 * @return true if the node was not already contained in the in-list
	 */
	public boolean addIn(Node v) {
		if (this.hasOut(v)) {
			this.neighbors.add(v);
		}
		return this.in.add(v);
	}

	/**
	 * removes the given node from the in-list
	 * 
	 * @param v
	 * @return true if the node was contained in the in-list
	 */
	public boolean removeIn(Node v) {
		if (this.hasNeighbor(v)) {
			this.neighbors.remove(v);
		}
		return this.in.remove(v);
	}

	private Set<Node> out;

	/**
	 * 
	 * @return set of nodes that this node has an outgoing edge to (out-list)
	 */
	public Set<Node> getOut() {
		return this.out;
	}

	/**
	 * 
	 * @param v
	 * @return true if the given node is contained in the out-list
	 */
	public boolean hasOut(Node v) {
		return this.out.contains(v);
	}

	/**
	 * adds the given node to the out-list
	 * 
	 * @param v
	 * @return true if the node was not already contained in the out-list
	 */
	public boolean addOut(Node v) {
		if (this.hasIn(v)) {
			this.neighbors.add(v);
		}
		return this.out.add(v);
	}

	/**
	 * removes the given node from the out-list
	 * 
	 * @param v
	 * @return true if the node was contained in the in-list
	 */
	public boolean removeOut(Node v) {
		if (this.hasNeighbor(v)) {
			this.neighbors.remove(v);
		}
		return this.out.remove(v);
	}

	private Set<Node> neighbors;

	/**
	 * 
	 * @return set of neighbors of this node, i.e., the intersection of in- and
	 *         out-list
	 */
	public Set<Node> getNeighbors() {
		return this.neighbors;
	}

	/**
	 * 
	 * @param v
	 * @return true if the given node is contained in the neighbor set
	 */
	public boolean hasNeighbor(Node v) {
		return this.neighbors.contains(v);
	}

	// public boolean equals(Object obj) {
	// return obj != null && obj instanceof Node
	// && this.index == ((Node) obj).index;
	// }
	//
	// public int hashCode(){
	// return this.index;
	// }
}
