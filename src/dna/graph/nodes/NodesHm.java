package dna.graph.nodes;

import java.util.Collection;
import java.util.HashMap;

import dna.graph.Edge;
import dna.graph.Node;

public class NodesHm<N extends Node<E>, E extends Edge>
		extends Nodes<N, E> {

	private HashMap<Integer, N> nodes;

	public NodesHm(int nodes) {
		this.nodes = new HashMap<Integer, N>(nodes);
	}

	@Override
	public N getNode(int index) {
		return this.nodes.get(index);
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
			return true;
		}
		return false;
	}

	@Override
	public boolean removeNode(N n) {
		return this.nodes.remove(n.getIndex()) != null;
	}

	@Override
	public boolean containsNode(N n) {
		return this.nodes.containsKey(n.getIndex());
	}

	@Override
	public N getRandomNode() {
		// TODO implement random directed node @hs
		return null;
	}

}
