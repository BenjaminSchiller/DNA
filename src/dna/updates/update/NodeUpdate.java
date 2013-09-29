package dna.updates.update;

import dna.graph.nodes.INode;

public abstract class NodeUpdate extends Update {

	protected INode node;

	public NodeUpdate(UpdateType type, INode node) {
		super(type);
		this.node = node;
	}

	public INode getNode() {
		return this.node;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.node.getStringRepresentation();
	}

	@Override
	protected String toString_() {
		return this.node.toString();
	}

}
