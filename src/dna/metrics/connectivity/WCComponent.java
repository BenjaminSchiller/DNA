package dna.metrics.connectivity;

import java.util.HashSet;

import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class WCComponent {
	private HashSet<Node> nodes = new HashSet<Node>();
	private HashSet<Edge> edges = new HashSet<Edge>();
	private int index;

	public WCComponent() {
		this.index = Integer.MAX_VALUE;
	}

	public WCComponent(Node n) {
		this.nodes.add(n);
		this.index = n.getIndex();
	}

	public void addNode(Node n) {
		this.nodes.add(n);
		if (n.getIndex() < this.index) {
			this.index = n.getIndex();
		}
	}

	public void removeNode(Node n) {
		this.nodes.remove(n);
		if (n.getIndex() <= this.index) {
			this.index = Integer.MAX_VALUE;
			for (Node n_ : this.nodes) {
				if (n_.getIndex() < this.index) {
					this.index = n_.getIndex();
				}
			}
		}
	}

	public boolean addNodes(HashSet<Node> nodes) {
		return this.nodes.addAll(nodes);
	}

	public boolean containsNode(Node n) {
		return this.nodes.contains(n);
	}

	public boolean addEdge(Edge e) {
		return this.edges.add(e);
	}

	public boolean removeEdge(Edge e) {
		return this.edges.remove(e);
	}

	public boolean containsEdge(Edge e) {
		return this.edges.contains(e);
	}

	public HashSet<Node> getNodes() {
		return this.nodes;
	}

	public HashSet<Edge> getEdges() {
		return this.edges;
	}

	public boolean addEdges(HashSet<Edge> edges) {
		return this.edges.addAll(edges);
	}

	public int getIndex() {
		return this.index;
	}

	public int size() {
		return this.nodes.size();
	}

	public String toString() {
		return "C" + this.index + ":" + this.nodes.size();
	}
}
