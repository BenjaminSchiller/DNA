package dna.graph.undirected;

import java.util.Collection;

import dna.graph.Node;

public abstract class UndirectedNode extends Node<UndirectedEdge> {
	public UndirectedNode(int index) {
		super(index);
	}
	
	public abstract Collection<UndirectedEdge> getEdges();

	public abstract int getDegree();
}
