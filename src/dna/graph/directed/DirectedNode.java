package dna.graph.directed;

import java.util.Collection;

import dna.graph.Node;

public abstract class DirectedNode extends Node<DirectedEdge> {

	public DirectedNode(int index) {
		super(index);
	}

	public abstract Collection<DirectedEdge> getIncomingEdges();

	public abstract Collection<DirectedEdge> getOutgoingEdges();

	public abstract int getInDegree();

	public abstract int getOutDegree();

}
