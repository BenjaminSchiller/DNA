package dna.graph.nodes;

import java.util.ArrayList;
import java.util.Collection;

import dna.graph.Edge;
import dna.graph.Node;
import dna.util.Rand;

public class NodesAl<N extends Node<E>, E extends Edge> extends Nodes<N, E> {

	private ArrayList<N> nodes;

	public NodesAl(int nodes) {
		this.nodes = new ArrayList<N>(nodes);
	}

	@Override
	public N getNode(int index) {
		N n = null;

		// check node at $index
		if (this.nodes.size() > index) {
			n = this.nodes.get(index);
			if (n != null && n.getIndex() == index) {
				return n;
			}
		}

		// check nodes before $index
		if (n == null || n.getIndex() > index) {
			for (int i = Math.min(index - 1, this.nodes.size() - 1); i >= 0; i--) {
				N n2 = this.nodes.get(i);
				if (n2 != null && n2.getIndex() == index) {
					return n2;
				}
			}
		}

		// check nodes after $index
		if (n == null || n.getIndex() < index) {
			for (int i = index + 1; i < this.nodes.size(); i++) {
				N n2 = this.nodes.get(i);
				if (n2 != null && n2.getIndex() == index) {
					return n2;
				}
			}
		}

		return null;
	}

	@Override
	public int getNodeCount() {
		return this.nodes.size();
	}

	@Override
	public Collection<N> getNodes() {
		return this.nodes;
	}

	@Override
	public boolean addNode(N n) {
		return !this.nodes.contains(n) && this.nodes.add(n);
	}

	@Override
	public boolean removeNode(N n) {
		return this.nodes.remove(n);
	}

	@Override
	public boolean containsNode(N n) {
		return this.nodes.contains(n);
	}

	@Override
	public N getRandomNode() {
		return this.nodes.get(Rand.rand.nextInt(this.nodes.size()));
	}
}
