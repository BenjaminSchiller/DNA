package dna.graph.undirected;

import java.util.ArrayList;
import java.util.Collection;

public class UndirectedGraphAlAl extends UndirectedGraphAl {

	private ArrayList<UndirectedEdge> edges;

	public UndirectedGraphAlAl(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, nodes);
		this.edges = new ArrayList<UndirectedEdge>(edges);
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
