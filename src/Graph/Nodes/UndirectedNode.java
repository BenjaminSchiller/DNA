package Graph.Nodes;

import DataStructures.GraphDataStructure;
import DataStructures.IEdgeListDatastructure;
import Graph.Edges.Edge;
import Graph.Edges.UndirectedEdge;

public class UndirectedNode extends Node {
	private IEdgeListDatastructure edges;
	public final static Class<? extends Edge> edgeType = UndirectedEdge.class;

	public UndirectedNode(int index, GraphDataStructure gds) {
		super(index, gds);
	}

	public UndirectedNode(String str, GraphDataStructure gds) {
		super(str, gds);
	}
	
	@Override
	protected void init() {
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
		return this.edges.removeEdge(e);
	}

	@Override
	public Iterable<Edge> getEdges() {
		return this.edges;
	}

}
