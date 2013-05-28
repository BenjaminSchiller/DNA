package dna.graph.undirected;

import dna.graph.Node;

public abstract class UndirectedNode extends Node<UndirectedEdge> {

	public UndirectedNode(int index) {
		super(index);
	}

	public UndirectedNode(String str) {
		super(str);
	}

	public abstract int getDegree();

	public void print() {
		System.out.println(this.toString());
		System.out.println("Edges: " + this.getEdges());
	}
}
