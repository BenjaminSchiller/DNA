package dna.graph;

import java.util.Collection;

public abstract class Graph<NodeType extends Node<EdgeType>, EdgeType extends Edge> {
	protected String name;

	public String getName() {
		return this.name;
	}

	protected long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	public Graph(String name, long timestamp) {
		this.name = name;
		this.timestamp = timestamp;
	}

	public String toString() {
		return this.getName() + " @ " + this.getTimestamp() + " ("
				+ this.getNodeCount() + "/" + this.getEdgeCount() + ")";
	}

	public abstract NodeType getNode(int index);

	public abstract int getNodeCount();

	public abstract Collection<NodeType> getNodes();

	public abstract boolean addNode(NodeType n);

	public abstract boolean removeNode(NodeType n);

	public abstract int getEdgeCount();

	public abstract Collection<EdgeType> getEdges();

	public abstract boolean addEdge(EdgeType e);

	public abstract boolean removeEdge(EdgeType e);
}
