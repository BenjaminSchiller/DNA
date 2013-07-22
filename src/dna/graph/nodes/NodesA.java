package dna.graph.nodes;

import java.util.Collection;
import java.util.LinkedList;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.util.Rand;

public class NodesA<N extends Node<E>, E extends Edge> extends Nodes<N, E> {

	private N[] nodes;

	private int nodeCount;

	@SuppressWarnings("unchecked")
	public NodesA(int nodes, GraphDatastructures<Graph<N, E>, N, E> ds) {
		super(ds);
		this.nodes = (N[]) new Node[nodes];
		this.nodeCount = 0;
	}

	@Override
	public N getNode(int index) {
		if (this.nodes.length <= index) {
			return null;
		}
		return this.nodes[index];
	}

	@Override
	public int getMaxNodeIndex() {
		return this.nodes.length - 1;
	}

	@Override
	public int getNodeCount() {
		return this.nodeCount;
	}

	@Override
	public Collection<N> getNodes() {
		Collection<N> collection = new LinkedList<N>();
		for (N n : this.nodes) {
			if (n != null) {
				collection.add(n);
			}
		}
		return collection;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addNode(N n) {
		if (this.nodes.length == 0) {
			this.nodes = (N[]) new Node[n.getIndex() + 1];
		}
		while (n.getIndex() >= this.nodes.length) {
			N[] nodes = (N[]) new Node[this.nodes.length * 2];
			System.arraycopy(this.nodes, 0, nodes, 0, this.nodes.length);
			this.nodes = nodes;
		}
		if (this.nodes[n.getIndex()] != null) {
			return false;
		}
		this.nodes[n.getIndex()] = n;
		this.nodeCount++;
		return true;
	}

	@Override
	public boolean removeNode(N n) {
		if (this.nodes.length <= n.getIndex()) {
			return false;
		}
		if (this.nodes[n.getIndex()] == null) {
			return false;
		}
		this.nodes[n.getIndex()] = null;

		this.nodeCount--;

		if (this.nodes[this.nodes.length - 1] != null) {
			return true;
		}
		int index = this.nodes.length - 1;
		for (int i = this.nodes.length - 1; i >= 0; i--) {
			if (this.nodes[i] != null) {
				break;
			}
			index--;
		}
		N[] nodesNew = this.ds.newNodeArray(index + 1);
		System.arraycopy(this.nodes, 0, nodesNew, 0, index + 1);
		this.nodes = nodesNew;

		return true;
	}

	@Override
	public boolean containsNode(N n) {
		return this.nodes.length > n.getIndex()
				&& this.nodes[n.getIndex()] != null;
	}

	@Override
	public N getRandomNode() {
		int index = Rand.rand.nextInt(this.nodes.length);
		while (this.nodes[index] == null) {
			index = Rand.rand.nextInt(this.nodes.length);
		}
		return this.nodes[index];
	}
}
