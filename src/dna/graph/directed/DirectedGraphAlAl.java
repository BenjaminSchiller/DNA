package dna.graph.directed;

import java.util.ArrayList;
import java.util.Collection;

public class DirectedGraphAlAl extends DirectedGraphAl {

	private ArrayList<DirectedEdge> edges;

	public DirectedGraphAlAl(String name, long timestamp, int nodes, int edges) {
		super(name, timestamp, nodes);
		this.edges = new ArrayList<DirectedEdge>(edges);
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
