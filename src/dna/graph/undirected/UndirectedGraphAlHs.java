package dna.graph.undirected;

import java.util.Collection;
import java.util.HashSet;

public class UndirectedGraphAlHs extends UndirectedGraphAl {

	private HashSet<UndirectedEdge> edges;

	public UndirectedGraphAlHs(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, nodes);
		this.edges = new HashSet<UndirectedEdge>(edges);
	}

	@Override
	public int getEdgeCount() {
		return this.edges.size();
	}

	@Override
	public Collection<UndirectedEdge> getEdges() {
		return this.edges;
	}

	@Override
	public boolean addEdge(UndirectedEdge e) {
		return e != null && this.edges.add(e);
	}

	@Override
	public boolean removeEdge(UndirectedEdge e) {
		return e != null && this.edges.remove(e);
	}

}
