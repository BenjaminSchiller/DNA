package dna.metrics.connectedComponents;

import dna.graph.nodes.UndirectedNode;

public class SpanningTreeNode {

	private UndirectedNode node;

	private SpanningTreeNode parent;
	private boolean root;
	private int weight;

	public SpanningTreeNode(UndirectedNode node) {
		this.node = node;
		this.parent = null;
		this.root = false;
		this.weight = 0;
	}

	public SpanningTreeNode getParent() {
		return parent;
	}

	public void setParent(SpanningTreeNode parent) {
		this.parent = parent;
	}

	public boolean isRoot() {
		return this.root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public UndirectedNode getNode() {
		return this.node;
	}

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
