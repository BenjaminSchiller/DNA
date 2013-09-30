package dna.updates.update;

import dna.graph.Graph;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;

public class NodeAddition extends NodeUpdate {

	public NodeAddition(INode node) {
		super(UpdateType.NODE_ADDITION, node);
	}

	@Override
	public boolean apply_(Graph g) {
		return g.addNode((Node) this.node);
	}

}
