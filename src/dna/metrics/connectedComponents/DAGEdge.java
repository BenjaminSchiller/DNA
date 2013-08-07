package dna.metrics.connectedComponents;

import java.util.HashSet;
import java.util.Set;

import dna.graph.directed.DirectedEdge;

public class DAGEdge {
	ComponentVertex src;
	ComponentVertex dst;
	Set<DirectedEdge> edges = new HashSet<DirectedEdge>();

	public DAGEdge(ComponentVertex src, ComponentVertex dst) {
		super();
		this.src = src;
		this.dst = dst;
	}

	public void addEdge(DirectedEdge edge) {
		this.edges.add(edge);
	}

	public void removeEdge(DirectedEdge edge) {
		this.edges.remove(edge);
	}

	public ComponentVertex getSrc() {
		return src;
	}

	public ComponentVertex getDst() {
		return dst;
	}

	public Set<DirectedEdge> getEdges() {
		return edges;
	}

}
