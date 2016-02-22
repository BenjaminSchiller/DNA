package dna.parallel.auxData;

import java.util.Set;

import dna.graph.edges.Edge;
import dna.parallel.partition.NonOverlappingPartition;

public class NonOverlappingAuxData extends AuxData<NonOverlappingPartition> {

	protected Set<Edge> edges;

	public NonOverlappingAuxData(Set<Edge> edges) {
		this.edges = edges;
	}

	public boolean addEdge(Edge e) {
		return this.edges.add(e);
	}

	public boolean removeEdge(Edge e) {
		return this.edges.remove(e);
	}

	public Set<Edge> getEdges() {
		return this.edges;
	}

	public String toString() {
		return "NonOverlappingAuxData: " + this.edges.size() + " edges";
	}
}
