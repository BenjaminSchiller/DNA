package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.util.Log;

public class NodeRemoval<E extends Edge> extends NodeUpdate<E> {

	public NodeRemoval(Node<E> node) {
		super(node, UpdateType.NodeRemoval);
	}

	public String toString() {
		return "remove " + this.node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean apply(Graph<? extends Node<E>, ? extends E> graph) {
		Log.debug("=> " + this.toString());
		boolean success = true;
		if (this.node instanceof DirectedNode) {
			DirectedNode node = (DirectedNode) this.node;
			for (DirectedEdge e : node.getOutgoingEdges()) {
				success &= e.getDst().removeEdge(e);
				success &= ((Graph<Node<E>, E>) graph).removeEdge((E) e);
			}
			for (DirectedEdge e : node.getIncomingEdges()) {
				success &= e.getSrc().removeEdge(e);
				success &= ((Graph<Node<E>, E>) graph).removeEdge((E) e);
			}
		} else if (this.node instanceof UndirectedNode) {
			UndirectedNode node = (UndirectedNode) this.node;
			for (UndirectedEdge e : node.getEdges()) {
				if (node.equals(e.getNode1())) {
					success &= e.getNode2().removeEdge(e);
				} else {
					success &= e.getNode1().removeEdge(e);
				}
				success &= ((Graph<Node<E>, E>) graph).removeEdge((E) e);
			}
		} else {
			return false;
		}
		success &= ((Graph<Node<E>, E>) graph).removeNode((Node<E>) this.node);
		return success;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.node.getStringRepresentation();
	}

}
