package dna.diff;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Edge;

public class Diff {
	public Diff(int nodes, long from, long to) {
		this.addedEdges = new HashSet<Edge>();
		this.removedEdges = new HashSet<Edge>();
		this.nodes = nodes;
		this.from = from;
		this.to = to;
	}

	public String toString() {
		return this.from + "=>" + this.to + " n=" + this.nodes + " ("
				+ this.addedEdges.size() + "/" + this.removedEdges.size() + ")";
	}

	public String getFilename() {
		return "data/d-" + this.nodes + "-" + this.from + "-" + this.to
				+ ".txt";
	}

	private Set<Edge> addedEdges;

	public Set<Edge> getAddedEdges() {
		return this.addedEdges;
	}

	public boolean addAddedEdges(Edge e) {
		this.removedEdges.remove(e);
		return this.addedEdges.add(e);
	}

	public boolean addsEdge(Edge e) {
		return this.addedEdges.contains(e);
	}

	private Set<Edge> removedEdges;

	public Set<Edge> getRemovedEdges() {
		return this.removedEdges;
	}

	public boolean addRemovedEdge(Edge e) {
		this.addedEdges.remove(e);
		return this.removedEdges.add(e);
	}

	public boolean removesEdge(Edge e) {
		return this.removedEdges.contains(e);
	}

	private int nodes;

	public int getNodes() {
		return this.nodes;
	}

	private long from;

	public long getFrom() {
		return this.from;
	}

	private long to;

	public long getTo() {
		return this.to;
	}
}
