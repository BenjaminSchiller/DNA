package dna.graph.nodes;

import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;

public class UndirectedNode extends Node {
	private IEdgeListDatastructure edges;

	public UndirectedNode(int index, GraphDataStructure gds) {
		super(index, gds);
	}

	public UndirectedNode(String str, GraphDataStructure gds) {
		super(str, gds);
	}

	@Override
	public void init() {
		this.edges = this.gds.newNodeEdgeList();
	}

	public int getDegree() {
		return this.edges.size();
	}

	public void print() {
		System.out.println(this.toString());
		System.out.println("Edges: " + this.getEdges());
	}

	@Override
	public boolean hasEdge(Edge e) {
		return this.edges.contains(e);
	}

	@Override
	public boolean addEdge(Edge e) {
		return !this.edges.contains(e) && this.edges.add(e);
	}

	@Override
	public boolean removeEdge(Edge e) {
		return this.edges.remove(e);
	}

	@Override
	public Iterable<IElement> getEdges() {
		return this.edges;
	}

	public String toString() {
		return super.toString() + " (" + this.edges.size() + ")";
	}

}
