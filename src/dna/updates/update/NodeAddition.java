package dna.updates.update;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;

public class NodeAddition extends NodeUpdate {

	public NodeAddition(INode node) {
		super(node);
	}

	public NodeAddition(String str, GraphDataStructure gds) {
		super(gds.newNodeInstance(str));
	}

	@Override
	public boolean apply_(IGraph g) {
		return g.addNode((Node) this.node);
	}

	@Override
	public UpdateType getType() {
		return UpdateType.NA;
	}

	@Override
	protected String asString_() {
		return this.node.asString();
	}

	@Override
	protected String toString_() {
		return this.node.toString();
	}

}
