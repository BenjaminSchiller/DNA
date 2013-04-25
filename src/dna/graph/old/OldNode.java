package dna.graph.old;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Represents a node with a list of incoming and outgoing edges as well as a
 * neighbor list (intersection of incoming and outgoing edges).
 * 
 * @author benni
 * 
 */
// TODO add list / sets of edges
public class OldNode {
	/**
	 * 
	 * @param index
	 *            index of the node as it is referenced in the graph and in
	 *            string representations
	 */
	public OldNode(int index) {
		this.index = index;
		this.in = new HashSet<OldNode>();
		this.out = new HashSet<OldNode>();
		this.neighbors = new HashSet<OldNode>();
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

	private Set<OldNode> in;

	/**
	 * 
	 * @return set of nodes that have an edge pointing to this node (in-list)
	 */
	public Set<OldNode> getIn() {
		return this.in;
	}

	/**
	 * 
	 * @param v
	 * @return true if the given node is in the in-list
	 */
	public boolean hasIn(OldNode v) {
		return this.in.contains(v);
	}

	/**
	 * adds the given node to the in-list
	 * 
	 * @param v
	 * @return true if the node was not already contained in the in-list
	 */
	public boolean addIn(OldNode v) {
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
	public boolean removeIn(OldNode v) {
		if (this.hasNeighbor(v)) {
			this.neighbors.remove(v);
		}
		return this.in.remove(v);
	}

	private Set<OldNode> out;

	/**
	 * 
	 * @return set of nodes that this node has an outgoing edge to (out-list)
	 */
	public Set<OldNode> getOut() {
		return this.out;
	}

	/**
	 * 
	 * @param v
	 * @return true if the given node is contained in the out-list
	 */
	public boolean hasOut(OldNode v) {
		return this.out.contains(v);
	}

	/**
	 * adds the given node to the out-list
	 * 
	 * @param v
	 * @return true if the node was not already contained in the out-list
	 */
	public boolean addOut(OldNode v) {
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
	public boolean removeOut(OldNode v) {
		if (this.hasNeighbor(v)) {
			this.neighbors.remove(v);
		}
		return this.out.remove(v);
	}

	private Set<OldNode> neighbors;

	/**
	 * 
	 * @return set of neighbors of this node, i.e., the intersection of in- and
	 *         out-list
	 */
	public Set<OldNode> getNeighbors() {
		return this.neighbors;
	}

	/**
	 * 
	 * @param v
	 * @return true if the given node is contained in the neighbor set
	 */
	public boolean hasNeighbor(OldNode v) {
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
