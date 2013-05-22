package dna.graph.nodes;

import java.util.Collection;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;

public abstract class Nodes<N extends Node<E>, E extends Edge> {

	protected GraphDatastructures<Graph<N, E>, N, E> ds;

	public Nodes(GraphDatastructures<Graph<N, E>, N, E> ds) {
		this.ds = ds;
	}

	public abstract N getNode(int index);

	public abstract int getMaxNodeIndex();

	public abstract int getNodeCount();

	public abstract Collection<N> getNodes();

	public abstract boolean addNode(N n);

	public abstract boolean removeNode(N n);

	public abstract boolean containsNode(N n);

	public abstract N getRandomNode();

}
