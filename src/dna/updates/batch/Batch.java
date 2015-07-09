package dna.updates.batch;

import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.Iterables;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;
import dna.util.Log;

public class Batch {
	private HashMap<INode, NodeAddition> nodeAdditions;

	private HashMap<INode, NodeRemoval> nodeRemovals;

	private HashMap<INode, NodeWeight> nodeWeights;

	private HashMap<IEdge, EdgeAddition> edgeAdditions;

	private HashMap<IEdge, EdgeRemoval> edgeRemovals;

	private HashMap<IEdge, EdgeWeight> edgeWeights;

	private Iterable<Update> all;

	private GraphDataStructure gds;

	private long from;

	private long to;

	public Batch(GraphDataStructure gds, long from, long to) {
		this(gds, from, to, 0, 0, 0, 0, 0, 0);
	}

	@SuppressWarnings("unchecked")
	public Batch(GraphDataStructure gds, long from, long to, int nodeAdditions,
			int nodeRemovals, int nodeWeights, int edgeAdditions,
			int edgeRemovals, int edgeWeights) {
		this.gds = gds;
		this.from = from;
		this.to = to;
		this.nodeAdditions = new HashMap<INode, NodeAddition>(nodeAdditions);
		this.nodeRemovals = new HashMap<INode, NodeRemoval>(nodeRemovals);
		this.nodeWeights = new HashMap<INode, NodeWeight>(nodeWeights);
		this.edgeAdditions = new HashMap<IEdge, EdgeAddition>(edgeAdditions);
		this.edgeRemovals = new HashMap<IEdge, EdgeRemoval>(edgeRemovals);
		this.edgeWeights = new HashMap<IEdge, EdgeWeight>(edgeWeights);
		this.all = Iterables.unmodifiableIterable(Iterables.concat(
				this.nodeAdditions.values(), this.nodeRemovals.values(),
				this.nodeWeights.values(), this.edgeAdditions.values(),
				this.edgeRemovals.values(), this.edgeWeights.values()));
	}

	/*
	 * APPLICATION
	 */

	public boolean apply(Graph g) {

		if (this.from != g.getTimestamp()) {
			throw new IllegalStateException("cannot apply batch "
					+ this.toString() + " to graph " + g.toString());
		}

		boolean success = true;

		success &= this.apply(g, this.nodeRemovals.values());
		success &= this.apply(g, this.edgeRemovals.values());

		success &= this.apply(g, this.nodeAdditions.values());
		success &= this.apply(g, this.edgeAdditions.values());

		success &= this.apply(g, this.nodeWeights.values());
		success &= this.apply(g, this.edgeWeights.values());

		g.setTimestamp(this.to);

		return success;
	}

	private boolean apply(Graph g, Iterable<? extends Update> updates) {
		boolean success = true;
		for (Update u : updates) {
			if (!u.apply(g)) {
				Log.error("cannot apply '" + u + "' to '" + g + "'");
				success = false;
			}
		}
		return success;
	}

	/*
	 * ADDING / REMOVING
	 */

	public boolean addAll(Iterable<? extends Update> updates) {
		boolean success = true;
		for (Update u : updates) {
			success &= this.add(u);
		}
		return success;
	}

	public boolean add(Update u) {
		if (u instanceof NodeAddition) {
			return this.add((NodeAddition) u);
		} else if (u instanceof NodeRemoval) {
			return this.add((NodeRemoval) u);
		} else if (u instanceof NodeWeight) {
			return this.add((NodeWeight) u);
		} else if (u instanceof EdgeAddition) {
			return this.add((EdgeAddition) u);
		} else if (u instanceof EdgeRemoval) {
			return this.add((EdgeRemoval) u);
		} else if (u instanceof EdgeWeight) {
			return this.add((EdgeWeight) u);
		}
		return false;
	}

	public boolean add(NodeAddition na) {
		return !this.nodeAdditions.containsKey(na.getNode())
				&& this.nodeAdditions.put(na.getNode(), na) == null;
	}

	public boolean add(NodeRemoval nr) {
		return !this.nodeRemovals.containsKey(nr.getNode())
				&& this.nodeRemovals.put(nr.getNode(), nr) == null;
	}

	public boolean add(NodeWeight nw) {
		return !this.nodeWeights.containsKey(nw.getNode())
				&& this.nodeWeights.put(nw.getNode(), nw) == null;
	}

	public boolean add(EdgeAddition ea) {
		return !this.edgeAdditions.containsKey(ea.getEdge())
				&& this.edgeAdditions.put(ea.getEdge(), ea) == null;
	}

	public boolean add(EdgeRemoval er) {
		return !this.edgeRemovals.containsKey(er.getEdge())
				&& this.edgeRemovals.put(er.getEdge(), er) == null;
	}

	public boolean add(EdgeWeight ew) {
		return !this.edgeWeights.containsKey(ew.getEdge())
				&& this.edgeWeights.put(ew.getEdge(), ew) == null;
	}

	public boolean removeAll(Iterable<? extends Update> updates) {
		boolean success = true;
		for (Update u : updates) {
			success &= this.remove(u);
		}
		return success;
	}

	public boolean remove(Update u) {
		if (u instanceof NodeAddition) {
			return this.remove((NodeAddition) u);
		} else if (u instanceof NodeRemoval) {
			return this.remove((NodeRemoval) u);
		} else if (u instanceof NodeWeight) {
			return this.remove((NodeWeight) u);
		} else if (u instanceof EdgeAddition) {
			return this.remove((EdgeAddition) u);
		} else if (u instanceof EdgeRemoval) {
			return this.remove((EdgeRemoval) u);
		} else if (u instanceof EdgeWeight) {
			return this.remove((EdgeWeight) u);
		}
		return false;
	}

	public boolean remove(NodeAddition na) {
		return this.nodeAdditions.remove(na.getNode()) != null;
	}

	public boolean remove(NodeRemoval nr) {
		return this.nodeRemovals.remove(nr.getNode()) != null;
	}

	public boolean remove(NodeWeight nw) {
		return this.nodeWeights.remove(nw.getNode()) != null;
	}

	public boolean remove(EdgeAddition ea) {
		return this.edgeAdditions.remove(ea.getEdge()) != null;
	}

	public boolean remove(EdgeRemoval er) {
		return this.edgeRemovals.remove(er.getEdge()) != null;
	}

	public boolean remove(EdgeWeight ew) {
		return this.edgeWeights.remove(ew.getEdge()) != null;
	}

	/*
	 * GET SPECIFIC UPDATE
	 */

	public NodeAddition getNodeAddition(Node n) {
		return this.nodeAdditions.get(n);
	}

	public NodeRemoval getNodeRemoval(Node n) {
		return this.nodeRemovals.get(n);
	}

	public NodeWeight getNodeWeight(IWeightedNode n) {
		return this.nodeWeights.get(n);
	}

	public EdgeAddition getEdgeAddition(Edge e) {
		return this.edgeAdditions.get(e);
	}

	public EdgeRemoval getEdgeRemoval(Edge e) {
		return this.edgeRemovals.get(e);
	}

	public EdgeWeight getEdgeWeight(IWeightedEdge e) {
		return this.edgeWeights.get(e);
	}

	/*
	 * GETTERS
	 */

	public Iterable<NodeAddition> getNodeAdditions() {
		return nodeAdditions.values();
	}

	public Iterable<NodeRemoval> getNodeRemovals() {
		return nodeRemovals.values();
	}

	public Iterable<NodeWeight> getNodeWeights() {
		return nodeWeights.values();
	}

	public Iterable<EdgeAddition> getEdgeAdditions() {
		return edgeAdditions.values();
	}

	public Iterable<EdgeRemoval> getEdgeRemovals() {
		return edgeRemovals.values();
	}

	public Iterable<EdgeWeight> getEdgeWeights() {
		return edgeWeights.values();
	}

	public Iterable<Update> getAllUpdates() {
		return this.all;
	}

	public GraphDataStructure getGraphDatastructures() {
		return this.gds;
	}

	public long getFrom() {
		return this.from;
	}

	public long getTo() {
		return this.to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	/*
	 * SIZE
	 */

	public int getNodeAdditionsCount() {
		return nodeAdditions.size();
	}

	public int getNodeRemovalsCount() {
		return nodeRemovals.size();
	}

	public int getNodeWeightsCount() {
		return nodeWeights.size();
	}

	public int getEdgeAdditionsCount() {
		return edgeAdditions.size();
	}

	public int getEdgeRemovalsCount() {
		return edgeRemovals.size();
	}

	public int getEdgeWeightsCount() {
		return edgeWeights.size();
	}

	public int getSize() {
		return this.nodeAdditions.size() + this.nodeRemovals.size()
				+ this.nodeWeights.size() + this.edgeAdditions.size()
				+ this.edgeRemovals.size() + this.edgeWeights.size();
	}

	/*
	 * EQUALITY
	 */

	// public boolean equals(Object otherO) {
	// if (!(otherO instanceof Batch))
	// return false;
	// Batch other = (Batch) otherO;
	//
	// if (this.getSize() != other.getSize())
	// return false;
	//
	// Iterable<Update> uOther = other.getAllUpdates();
	// for (Update u : uOther) {
	// if (!this.nodeAdditions.contains(u)
	// && !this.nodeRemovals.contains(u)
	// && !this.nodeWeights.contains(u)
	// && !this.edgeAdditions.contains(u)
	// && !this.edgeRemovals.contains(u)
	// && !this.edgeWeights.contains(u)) {
	// return false;
	// }
	// }
	//
	// return true;
	// }

	/*
	 * IO
	 */

	public String toString() {
		return "B " + this.from + " -> " + to + " ("
				+ this.getNodeAdditionsCount() + ","
				+ this.getNodeRemovalsCount() + ","
				+ this.getNodeWeightsCount() + "/"
				+ this.getEdgeAdditionsCount() + ","
				+ this.getEdgeRemovalsCount() + ","
				+ this.getEdgeWeightsCount() + ")";
	}

	public void print() {
		this.print(this.nodeAdditions.values(), "Node Additions");
		this.print(this.nodeRemovals.values(), "Node Reovals");
		this.print(this.nodeWeights.values(), "Node Weights");
		this.print(this.edgeAdditions.values(), "Edge Additions");
		this.print(this.edgeRemovals.values(), "Edge Removals");
		this.print(this.edgeWeights.values(), "Edge Weights");
	}

	private void print(Collection<? extends Update> updates, String name) {
		if (updates.size() == 0) {
			return;
		}
		System.out.println(name + ": " + updates.size());
		for (Update u : updates) {
			System.out.println("  " + u);
		}
	}
}
