package dna.graph.old;

import java.util.HashSet;
import java.util.Set;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;

public class OldGraph {
	/**
	 * 
	 * @param name
	 *            name of the graph
	 * @param nodes
	 *            number of nodes in the graph
	 * @param timestamp
	 *            timestamp indicating the point in time this graph was recorded
	 *            / generated
	 */
	public OldGraph(String name, int nodes, long timestamp) {
		this.name = name;
		this.nodes = new OldNode[nodes];
		for (int i = 0; i < nodes; i++) {
			this.nodes[i] = new OldNode(i);
		}
		this.edges = new HashSet<OldEdge>();
		this.timestamp = timestamp;
	}

	public String toString() {
		return this.name + " @ " + this.timestamp + " (N=" + this.nodes.length
				+ "/E=" + this.edges.size() + ")";
	}

	/**
	 * applies the given diff to this graph, i.e., add / remove the respective
	 * edges and change the timestamp
	 * 
	 * @param d
	 * @throws DiffNotApplicableException
	 */
	public void apply(Diff d) throws DiffNotApplicableException {
		if (!this.isApplicable(d)) {
			throw new DiffNotApplicableException(this, d);
		}

		this.timestamp = d.getTo();

		for (OldEdge e : d.getAddedEdges()) {
			this.addEdge(e);
		}

		for (OldEdge e : d.getRemovedEdges()) {
			this.removeEdge(e);
		}
	}

	private OldNode[] nodes;

	/**
	 * 
	 * @return list of nodes in the graph
	 */
	public OldNode[] getNodes() {
		return this.nodes;
	}

	/**
	 * 
	 * @param index
	 * @return node with the given index
	 */
	public OldNode getNode(int index) {
		return this.nodes[index];
	}

	private Set<OldEdge> edges;

	/**
	 * 
	 * @return set of edges in the graph
	 */
	public Set<OldEdge> getEdges() {
		return this.edges;
	}

	/**
	 * 
	 * @param e
	 * @return true if the given edge is contained in the graph
	 */
	public boolean containsEdge(OldEdge e) {
		return this.edges.contains(e);
	}

	/**
	 * adds the given edge to the graph
	 * 
	 * @param e
	 * @return true if the edge was not already contained in the edge set
	 */
	public boolean addEdge(OldEdge e) {
		if (!this.edges.add(e)) {
			return false;
		}
		e.getSrc().addOut(e.getDst());
		e.getDst().addIn(e.getSrc());
		return true;
	}

	/**
	 * removed the given edge from the edge set
	 * 
	 * @param e
	 * @return true if the edge was contained in the edge set
	 */
	public boolean removeEdge(OldEdge e) {
		if (!this.edges.remove(e)) {
			return false;
		}
		e.getSrc().removeOut(e.getDst());
		e.getDst().removeIn(e.getSrc());
		return true;
	}

	private String name;

	/**
	 * 
	 * @return name of the graph
	 */
	public String getName() {
		return this.name;
	}

	private long timestamp;

	/**
	 * 
	 * @return timestamp indicating the point in time this graph was recorded /
	 *         generated
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * a diff is applicable iff the number of nodes are the same and the graph's
	 * timestamp equals the diff's from
	 * 
	 * @param d
	 * @return true if the given diff is applicable to this graph
	 */
	public boolean isApplicable(Diff d) {
		return this.getNodes().length == d.getNodes()
				&& d.getFrom() == this.getTimestamp();
	}
}
