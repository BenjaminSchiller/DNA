package dna.graph.undirected;

import dna.graph.Node;

public abstract class UndirectedNode extends Node<UndirectedEdge> {
	public UndirectedNode(int index) {
		super(index);
	}

	public abstract int getDegree();

	public void print() {
		System.out.println(this.toString());
		System.out.println("Edges: " + this.getEdges());
	}
}
