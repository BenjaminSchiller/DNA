package dna.graph.undirected;

import java.util.HashSet;

public class UndirectedNodeHs extends UndirectedNode {

	private HashSet<UndirectedEdge> edges;

	public UndirectedNodeHs(int index) {
		super(index);
		this.edges = new HashSet<UndirectedEdge>();
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
		return this.edges.add(e);
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
