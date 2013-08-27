package dna.graph.nodes;

import java.util.Collection;
import java.util.HashMap;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.util.Rand;

public class NodesHm<N extends Node<E>, E extends Edge> extends Nodes<N, E> {

	private HashMap<Integer, N> nodes;

	private int maxIndex;

	public NodesHm(int nodes, GraphDatastructures<Graph<N, E>, N, E> ds) {
		super(ds);
		this.nodes = new HashMap<Integer, N>(nodes);
		this.maxIndex = -1;
	}

	@Override
	public N getNode(int index) {
		return this.nodes.get(index);
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxIndex;
	}

	@Override
	public int getNodeCount() {
		return this.nodes.size();
	}

	@Override
	public Collection<N> getNodes() {
		return this.nodes.values();
	}

	@Override
	public boolean addNode(N n) {
		if (!this.nodes.containsKey(n.getIndex())) {
			this.nodes.put(n.getIndex(), n);
			if (n.getIndex() > this.maxIndex) {
				this.maxIndex = n.getIndex();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean removeNode(N n) {
		if (this.nodes.remove(n.getIndex()) == null) {
			return false;
		}
		if (n.getIndex() == this.maxIndex) {
			int max = this.maxIndex - 1;
			while (!this.nodes.containsKey(max)) {
				max--;
			}
			this.maxIndex = max;
		}
		return true;
	}

	@Override
	public boolean containsNode(N n) {
		return this.nodes.containsKey(n.getIndex());
	}

	@Override
	public N getRandomNode() {
		int index = Rand.rand.nextInt(this.nodes.size());
		int counter = 0;
		for (N node : this.nodes.values()) {
			if (counter == index) {
				return node;
			}
			counter++;
		}
		return null;
	}

}
