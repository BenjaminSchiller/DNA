package dna.diff;

import java.util.HashSet;
import java.util.Set;

import dna.graph.old.OldEdge;

public class Diff {
	public Diff(int nodes, long from, long to) {
		this.addedEdges = new HashSet<OldEdge>();
		this.removedEdges = new HashSet<OldEdge>();
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

	private Set<OldEdge> addedEdges;

	public Set<OldEdge> getAddedEdges() {
		return this.addedEdges;
	}

	public boolean addAddedEdges(OldEdge e) {
		this.removedEdges.remove(e);
		return this.addedEdges.add(e);
	}

	public boolean addsEdge(OldEdge e) {
		return this.addedEdges.contains(e);
	}

	private Set<OldEdge> removedEdges;

	public Set<OldEdge> getRemovedEdges() {
		return this.removedEdges;
	}

	public boolean addRemovedEdge(OldEdge e) {
		this.addedEdges.remove(e);
		return this.removedEdges.add(e);
	}

	public boolean removesEdge(OldEdge e) {
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
