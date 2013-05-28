package dna.graph.undirected;

import java.util.ArrayList;

public class UndirectedNodeAl extends UndirectedNode {

	private ArrayList<UndirectedEdge> edges;

	public UndirectedNodeAl(int index) {
		super(index);
		this.edges = new ArrayList<UndirectedEdge>();
	}

	public UndirectedNodeAl(String str) {
		super(str);
		this.edges = new ArrayList<UndirectedEdge>();
	}

	@Override
	public int getDegree() {
		return this.edges.size();
	}

	@Override
	public boolean hasEdge(UndirectedEdge e) {
		return this.edges.contains(e);
	}

	@Override
	public boolean addEdge(UndirectedEdge e) {
		return !this.edges.contains(e) && this.edges.add(e);
	}

	@Override
	public boolean removeEdge(UndirectedEdge e) {
		return this.edges.remove(e);
	}

	@Override
	public Iterable<UndirectedEdge> getEdges() {
		return this.edges;
	}

}
