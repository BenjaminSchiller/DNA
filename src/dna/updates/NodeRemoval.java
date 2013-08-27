package dna.updates;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.util.Log;

public class NodeRemoval<E extends Edge> extends NodeUpdate<E> {

	public NodeRemoval(Node node) {
		super(node, UpdateType.NodeRemoval);
	}

	public String toString() {
		return "remove " + this.node;
	}

	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		boolean success = true;
		if (this.node instanceof DirectedNode) {
			DirectedNode node = (DirectedNode) this.node;
			for (IElement e : node.getOutgoingEdges()) {
				success &= ((DirectedEdge) e).getDst().removeEdge((Edge) e);
				success &= graph.removeEdge((Edge) e);
			}
			for (IElement e : node.getIncomingEdges()) {
				success &= ((DirectedEdge) e).getSrc().removeEdge((Edge) e);
				success &= graph.removeEdge((Edge) e);
			}
		} else if (this.node instanceof UndirectedNode) {
			UndirectedNode node = (UndirectedNode) this.node;
			for (IElement eTemp : node.getEdges()) {
				UndirectedEdge e = (UndirectedEdge) eTemp;
				if (node.equals(e.getNode1())) {
					success &= e.getNode2().removeEdge(e);
				} else {
					success &= e.getNode1().removeEdge(e);
				}
				success &= graph.removeEdge(e);
			}
		} else {
			return false;
		}
		success &= graph.removeNode(this.node);
		return success;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.node.getStringRepresentation();
	}

}
