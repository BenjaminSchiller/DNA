package dna.graph.directed;

import dna.graph.Node;

public abstract class DirectedNode extends Node<DirectedEdge> {

	public DirectedNode(int index) {
		super(index);
	}

	public abstract Iterable<DirectedEdge> getIncomingEdges();

	public abstract Iterable<DirectedEdge> getOutgoingEdges();

	public abstract int getDegree();

	public abstract int getInDegree();

	public abstract int getOutDegree();

	public void print() {
		System.out.println(this.toString());
		System.out.println("In: " + this.getIncomingEdges());
		System.out.println("Out: " + this.getOutgoingEdges());
	}

}
