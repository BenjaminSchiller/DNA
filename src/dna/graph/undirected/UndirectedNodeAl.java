package dna.graph.undirected;

import java.util.ArrayList;
import java.util.Collection;

public class UndirectedNodeAl extends UndirectedNode {

	private ArrayList<UndirectedEdge> edges;

	public UndirectedNodeAl(int index) {
		super(index);
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
		if (!this.hasEdge(e)) {
			this.edges.add(e);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeEdge(UndirectedEdge e) {
		return this.edges.remove(e);
	}

	@Override
	public Collection<UndirectedEdge> getEdges() {
		return this.edges;
	}

}
