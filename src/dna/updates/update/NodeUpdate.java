package dna.updates.update;

import dna.graph.nodes.INode;

public abstract class NodeUpdate extends Update {

	protected INode node;

	public INode getNode() {
		return this.node;
	}

	public NodeUpdate(INode node) {
		this.node = node;
	}

	@Override
	protected int hashCode_() {
		return this.node.hashCode();
	}

}
