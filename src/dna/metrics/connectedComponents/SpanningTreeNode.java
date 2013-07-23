package dna.metrics.connectedComponents;

import java.util.HashSet;
import java.util.Set;

import dna.graph.undirected.UndirectedNode;

public class SpanningTreeNode {

	private UndirectedNode node;

	private SpanningTreeNode parent;
	private Set<SpanningTreeNode> children;
	private boolean root;
	private int weight;

	public SpanningTreeNode(UndirectedNode node) {
		this.node = node;
		this.children = new HashSet<>();
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

	public Set<SpanningTreeNode> getChildren() {
		return children;
	}

	public void setChildren(Set<SpanningTreeNode> children) {
		this.children = children;
	}

	public UndirectedNode getNode() {
		return this.node;
	}

	public void addChild(SpanningTreeNode child) {
		this.children.add(child);
	}

	public void removeChild(SpanningTreeNode child) {
		this.children.remove(child);
	}

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
