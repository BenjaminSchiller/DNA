package dna.updates.batch;

import java.util.HashSet;

import com.google.common.collect.Iterables;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;

public class Batch {
	private HashSet<NodeAddition> nodeAdditions;

	private HashSet<NodeRemoval> nodeRemovals;

	private HashSet<NodeWeight> nodeWeights;

	private HashSet<EdgeAddition> edgeAdditions;

	private HashSet<EdgeRemoval> edgeRemovals;

	private HashSet<EdgeWeight> edgeWeights;

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
		this.nodeAdditions = new HashSet<NodeAddition>(nodeAdditions);
		this.nodeRemovals = new HashSet<NodeRemoval>(nodeRemovals);
		this.nodeWeights = new HashSet<NodeWeight>(nodeWeights);
		this.edgeAdditions = new HashSet<EdgeAddition>(edgeAdditions);
		this.edgeRemovals = new HashSet<EdgeRemoval>(edgeRemovals);
		this.edgeWeights = new HashSet<EdgeWeight>(edgeWeights);
		this.all = Iterables.unmodifiableIterable(Iterables.concat(
				this.nodeAdditions, this.nodeRemovals, this.nodeWeights,
				this.edgeAdditions, this.edgeRemovals, this.edgeWeights));
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

		success &= this.apply(g, this.nodeRemovals);
		success &= this.apply(g, this.edgeRemovals);

		success &= this.apply(g, this.nodeAdditions);
		success &= this.apply(g, this.edgeAdditions);

		success &= this.apply(g, this.nodeWeights);
		success &= this.apply(g, this.edgeWeights);

		g.setTimestamp(this.to);

		return success;
	}

	private boolean apply(Graph g, Iterable<? extends Update> updates) {
		boolean success = true;
		for (Update u : updates) {
			success &= u.apply(g);
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
			return this.nodeAdditions.add((NodeAddition) u);
		} else if (u instanceof NodeRemoval) {
			return this.nodeRemovals.add((NodeRemoval) u);
		} else if (u instanceof NodeWeight) {
			return this.nodeWeights.add((NodeWeight) u);
		} else if (u instanceof EdgeAddition) {
			return this.edgeAdditions.add((EdgeAddition) u);
		} else if (u instanceof EdgeRemoval) {
			return this.edgeRemovals.add((EdgeRemoval) u);
		} else if (u instanceof EdgeWeight) {
			return this.edgeWeights.add((EdgeWeight) u);
		}
		return false;
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
			return this.nodeAdditions.remove(u);
		} else if (u instanceof NodeRemoval) {
			return this.nodeRemovals.remove(u);
		} else if (u instanceof NodeWeight) {
			return this.nodeWeights.remove(u);
		} else if (u instanceof EdgeAddition) {
			return this.edgeAdditions.remove(u);
		} else if (u instanceof EdgeRemoval) {
			return this.edgeRemovals.remove(u);
		} else if (u instanceof EdgeWeight) {
			return this.edgeWeights.remove(u);
		}
		return false;
	}

	/*
	 * GETTERS
	 */

	public Iterable<NodeAddition> getNodeAdditions() {
		return nodeAdditions;
	}

	public Iterable<NodeRemoval> getNodeRemovals() {
		return nodeRemovals;
	}

	public Iterable<NodeWeight> getNodeWeights() {
		return nodeWeights;
	}

	public Iterable<EdgeAddition> getEdgeAdditions() {
		return edgeAdditions;
	}

	public Iterable<EdgeRemoval> getEdgeRemovals() {
		return edgeRemovals;
	}

	public Iterable<EdgeWeight> getEdgeWeights() {
		return edgeWeights;
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

	public boolean equals(Object otherO) {
		if (!(otherO instanceof Batch))
			return false;
		Batch other = (Batch) otherO;

		if (this.getSize() != other.getSize())
			return false;

		Iterable<Update> uOther = other.getAllUpdates();
		for (Update u : uOther) {
			if (!this.nodeAdditions.contains(u)
					&& !this.nodeRemovals.contains(u)
					&& !this.nodeWeights.contains(u)
					&& !this.edgeAdditions.contains(u)
					&& !this.edgeRemovals.contains(u)
					&& !this.edgeWeights.contains(u)) {
				return false;
			}
		}

		return true;
	}

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
		this.print(this.nodeAdditions, "Node Additions");
		this.print(this.nodeRemovals, "Node Reovals");
		this.print(this.nodeWeights, "Node Weights");
		this.print(this.edgeAdditions, "Edge Additions");
		this.print(this.edgeRemovals, "Edge Removals");
		this.print(this.edgeWeights, "Edge Weights");
	}

	private void print(HashSet<? extends Update> updates, String name) {
		if (updates.size() == 0) {
			return;
		}
		System.out.println(name + ": " + updates.size());
		for (Update u : updates) {
			System.out.println("  " + u);
			// System.out.println("  " + u.getStringRepresentation());
		}
	}
}
