package dna.graph;

import java.util.Collection;

import dna.graph.edges.Edges;
import dna.graph.nodes.Nodes;

public class GraphImpl<N extends Node<E>, E extends Edge> extends Graph<N, E> {

	private Nodes<N, E> nodes;

	private Edges<E> edges;

	public GraphImpl(String name, long timestamp,
			GraphDatastructures<Graph<N, E>, N, E> ds, Nodes<N, E> nodes,
			Edges<E> edges) {
		super(name, timestamp, ds);
		this.nodes = nodes;
		this.edges = edges;
	}

	@Override
	public N getNode(int index) {
		return this.nodes.getNode(index);
	}

	@Override
	public int getMaxNodeIndex() {
		return this.nodes.getMaxNodeIndex();
	}

	@Override
	public int getNodeCount() {
		return this.nodes.getNodeCount();
	}

	@Override
	public Collection<N> getNodes() {
		return this.nodes.getNodes();
	}

	@Override
	public boolean addNode(N n) {
		return this.nodes.addNode(n);
	}

	@Override
	public boolean removeNode(N n) {
		if (!this.nodes.removeNode(n)) {
			return false;
		}
		boolean success = true;
		for (E e : n.getEdges()) {
			success &= this.edges.removeEdge(e);
		}
		return success;
	}

	@Override
	public boolean containsNode(N n) {
		return this.nodes.containsNode(n);
	}

	@Override
	public Node<E> getRandomNode() {
		return this.nodes.getRandomNode();
	}

	@Override
	public E getEdge(E e) {
		return this.edges.getEdge(e);
	}

	@Override
	public int getEdgeCount() {
		return this.edges.getEdgeCount();
	}

	@Override
	public Collection<E> getEdges() {
		return this.edges.getEdges();
	}

	@Override
	public boolean addEdge(E e) {
		return this.edges.addEdge(e);
	}

	@Override
	public boolean removeEdge(E e) {
		return this.edges.removeEdge(e);
	}

	@Override
	public boolean containsEdge(E e) {
		return this.edges.containsEdge(e);
	}

	@Override
	public E getRandomEdge() {
		return this.edges.getRandomEdge();
	}

}
