package dna.updates;

import java.util.ArrayList;

import com.google.common.collect.Iterables;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.undirected.UndirectedGraphDatastructures;

public class Batch<E extends Edge> {

	private ArrayList<Update<E>> nodeAdditions;

	private ArrayList<Update<E>> nodeRemovals;

	private ArrayList<Update<E>> nodeWeightUpdates;

	private ArrayList<Update<E>> edgeAdditions;

	private ArrayList<Update<E>> edgeRemovals;

	private ArrayList<Update<E>> edgeWeightUpdates;

	private Iterable<Update<E>> all;

	private GraphDatastructures<Graph<Node<E>, E>, Node<E>, E> ds;

	private long from;

	private long to;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Batch(DirectedGraphDatastructures ds, long from, long to) {
		this((GraphDatastructures) ds, from, to);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Batch(UndirectedGraphDatastructures ds, long from, long to) {
		this((GraphDatastructures) ds, from, to);
	}

	public Batch(GraphDatastructures<Graph<Node<E>, E>, Node<E>, E> ds,
			long from, long to) {
		this(ds, from, to, 0, 0, 0, 0, 0, 0);
	}

	@SuppressWarnings("unchecked")
	public Batch(GraphDatastructures<Graph<Node<E>, E>, Node<E>, E> ds,
			long from, long to, int nodeAdditions, int nodeRemovals,
			int nodeWeightUpdates, int edgeAdditions, int edgeRemovals,
			int edgeWeightUpdates) {
		this.nodeAdditions = new ArrayList<Update<E>>(nodeAdditions);
		this.nodeRemovals = new ArrayList<Update<E>>(nodeRemovals);
		this.nodeWeightUpdates = new ArrayList<Update<E>>(nodeWeightUpdates);
		this.edgeAdditions = new ArrayList<Update<E>>(edgeAdditions);
		this.edgeRemovals = new ArrayList<Update<E>>(edgeRemovals);
		this.edgeWeightUpdates = new ArrayList<Update<E>>(edgeWeightUpdates);
		this.all = Iterables.unmodifiableIterable(Iterables.concat(
				this.nodeAdditions, this.nodeRemovals, this.nodeWeightUpdates,
				this.edgeAdditions, this.edgeRemovals, this.edgeWeightUpdates));
		this.ds = ds;
		this.from = from;
		this.to = to;
	}

	public String toString() {
		return "B " + this.from + " -> " + to + " ("
				+ this.getNodeAdditionCount() + ","
				+ this.getNodeRemovalCount() + ","
				+ this.getNodeWeightUpdateCount() + "/"
				+ this.getEdgeAdditionCount() + ","
				+ this.getEdgeRemovalCount() + ","
				+ this.getEdgeWeightUpdateCount() + ")";
	}

	public boolean addAll(Iterable<Update<E>> updates) {
		boolean success = true;
		for (Update<E> u : updates) {
			success &= this.add(u);
		}
		return success;
	}

	public boolean add(Update<E> update) {
		if (update instanceof NodeAddition<?>) {
			this.nodeAdditions.add((NodeAddition<E>) update);
			return true;
		}
		if (update instanceof NodeRemoval<?>) {
			this.nodeRemovals.add((NodeRemoval<E>) update);
			return true;
		}
		if (update instanceof NodeWeightUpdate<?>) {
			this.nodeWeightUpdates.add((NodeWeightUpdate<E>) update);
			return true;
		}
		if (update instanceof EdgeAddition<?>) {
			this.edgeAdditions.add((EdgeAddition<E>) update);
			return true;
		}
		if (update instanceof EdgeRemoval<?>) {
			this.edgeRemovals.add((EdgeRemoval<E>) update);
			return true;
		}
		if (update instanceof EdgeWeightUpdate<?>) {
			this.edgeWeightUpdates.add((EdgeWeightUpdate<E>) update);
			return true;
		}
		return false;
	}

	public Iterable<Update<E>> getNodeAdditions() {
		return nodeAdditions;
	}

	public Iterable<Update<E>> getNodeRemovals() {
		return nodeRemovals;
	}

	public Iterable<Update<E>> getNodeWeightUpdates() {
		return nodeWeightUpdates;
	}

	public Iterable<Update<E>> getEdgeAdditions() {
		return edgeAdditions;
	}

	public Iterable<Update<E>> getEdgeRemovals() {
		return edgeRemovals;
	}

	public Iterable<Update<E>> getEdgeWeightUpdates() {
		return edgeWeightUpdates;
	}

	public Iterable<Update<E>> getAllUpdates() {
		return this.all;
	}

	public int getNodeAdditionCount() {
		return nodeAdditions.size();
	}

	public int getNodeRemovalCount() {
		return nodeRemovals.size();
	}

	public int getNodeWeightUpdateCount() {
		return nodeWeightUpdates.size();
	}

	public int getEdgeAdditionCount() {
		return edgeAdditions.size();
	}

	public int getEdgeRemovalCount() {
		return edgeRemovals.size();
	}

	public int getEdgeWeightUpdateCount() {
		return edgeWeightUpdates.size();
	}

	public int getSize() {
		return this.nodeAdditions.size() + this.nodeRemovals.size()
				+ this.nodeWeightUpdates.size() + this.edgeAdditions.size()
				+ this.edgeRemovals.size() + this.edgeWeightUpdates.size();
	}

	public GraphDatastructures<Graph<Node<E>, E>, Node<E>, E> getGraphDatastructures() {
		return this.ds;
	}

	public long getFrom() {
		return this.from;
	}

	public long getTo() {
		return this.to;
	}

	public void print() {
		this.print(this.nodeAdditions);
		this.print(this.nodeRemovals);
		this.print(this.nodeWeightUpdates);
		this.print(this.edgeAdditions);
		this.print(this.edgeRemovals);
		this.print(this.edgeWeightUpdates);
	}

	private void print(ArrayList<? extends Update<E>> list) {
		for (Update<E> u : list) {
			System.out.println(u);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean apply(Graph<? extends Node<E>, ? extends E> graph) {
		boolean success = true;
		Graph<Node<E>, E> g = (Graph<Node<E>, E>) graph;
		success &= this.apply(g, this.nodeAdditions);
		return success;
	}

	private boolean apply(Graph<Node<E>, E> g, ArrayList<Update<E>> updates) {
		boolean success = true;
		for (Update<E> u : updates) {
			success &= u.apply(g);
		}
		return success;
	}

}
