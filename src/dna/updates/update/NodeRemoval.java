package dna.updates.update;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.util.Log;

public class NodeRemoval extends NodeUpdate {

	public NodeRemoval(INode node) {
		super(node);
	}

	public NodeRemoval(String str, IGraph g) {
		super(g.getNode(Integer.parseInt(str)));
	}

	@Override
	public boolean apply_(IGraph g) {
		boolean success = true;
		if (this.node instanceof DirectedNode) {
			DirectedNode node = (DirectedNode) this.node;
			for (IElement e : node.getOutgoingEdges()) {
				success &= ((DirectedEdge) e).getDst().removeEdge((Edge) e);
				success &= g.removeEdge((Edge) e);
			}
			for (IElement e : node.getIncomingEdges()) {
				success &= ((DirectedEdge) e).getSrc().removeEdge((Edge) e);
				success &= g.removeEdge((Edge) e);
			}
		} else if (this.node instanceof UndirectedNode) {
			UndirectedNode node = (UndirectedNode) this.node;
			for (IElement eTemp : node.getEdges()) {
				UndirectedEdge e = (UndirectedEdge) eTemp;
				success &= e.getDifferingNode(node).removeEdge(e);
				success &= g.removeEdge(e);
			}
		} else {
			Log.error("attempting to remove unsupported node type "
					+ this.node.getClass());
			return false;
		}
		success &= g.removeNode((Node) this.node);
		return success;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.NR;
	}

	@Override
	protected String asString_() {
		return Integer.toString(this.node.getIndex());
	}

	@Override
	protected String toString_() {
		return Integer.toString(this.node.getIndex());
	}

}
