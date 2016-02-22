package dna.parallel.auxData;

import java.util.Set;

import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.parallel.partition.OverlappingPartition;

public class OverlappingAuxData extends AuxData<OverlappingPartition> {
	protected Set<Node> nodes;
	protected Set<Edge> edges;

	public OverlappingAuxData(Set<Node> nodes, Set<Edge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public Set<Node> getNodes() {
		return this.nodes;
	}

	public boolean addNode(Node n) {
		return this.nodes.add(n);
	}

	public boolean removeNode(Node n) {
		return this.nodes.remove(n);
	}

	public Set<Edge> getEdges() {
		return this.edges;
	}

	public boolean addEdge(Edge e) {
		return this.edges.add(e);
	}

	public boolean remobeEdge(Edge e) {
		return this.edges.remove(e);
	}

	public String toString() {
		return "NonOverlappingAuxData: " + this.nodes.size() + " nodes, "
				+ this.edges.size() + " edges";
	}
}
