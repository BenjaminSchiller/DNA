package dna.graph.directed;

import java.util.Collection;
import java.util.HashSet;

public class DirectedGraphAlHs extends DirectedGraphAl {

	private HashSet<DirectedEdge> edges;

	public DirectedGraphAlHs(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, nodes);
		this.edges = new HashSet<DirectedEdge>(edges);
	}

	@Override
	public int getEdgeCount() {
		return this.edges.size();
	}

	@Override
	public Collection<DirectedEdge> getEdges() {
		return this.edges;
	}

	@Override
	public boolean addEdge(DirectedEdge e) {
		return e != null && this.edges.add(e);
	}

	@Override
	public boolean removeEdge(DirectedEdge e) {
		return e != null && this.edges.remove(e);
	}

}
