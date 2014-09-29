package dna.depr.metrics.connectivity;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import dna.depr.metrics.Metric;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metricsNew.MetricNew;
import dna.util.parameters.Parameter;

/**
 * 
 * Abstract super class of weak connectivity. For undirected graphs, this is the
 * same as strong connectivity. For directed graph, the components are formed by
 * nodes that have a path between them when replacing all directed edges with
 * undirected ones.
 * 
 * @author benni
 * 
 */
public abstract class WeakConnectivity extends Connectivity {

	public WeakConnectivity(String name, ApplicationType type,
			MetricNew.MetricType metricType, Parameter... p) {
		super(name, type, metricType, p);
	}

	protected Collection<Node> getConnectedNodes(Node node) {
		HashSet<Node> nodes = new HashSet<Node>();
		nodes.add(node);

		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(node);

		while (!queue.isEmpty()) {
			Node current = queue.pop();
			nodes.add(current);
			if (current instanceof DirectedNode) {
				for (IElement out : ((DirectedNode) current).getOutgoingEdges()) {
					Node n = ((DirectedEdge) out).getDst();
					if (!nodes.contains(n)) {
						nodes.add(n);
						queue.add(n);
					}
				}
				for (IElement in : ((DirectedNode) current).getIncomingEdges()) {
					Node n = ((DirectedEdge) in).getSrc();
					if (!nodes.contains(n)) {
						nodes.add(n);
						queue.add(n);
					}
				}
			} else if (current instanceof UndirectedNode) {
				for (IElement e : ((UndirectedNode) current).getEdges()) {
					Node n = ((UndirectedEdge) e).getDifferingNode(current);
					if (!nodes.contains(n)) {
						nodes.add(n);
						queue.add(n);
					}
				}
			}
		}

		return nodes;
	}

	protected Collection<Node> getConnectedNodes(Node src, Node dst) {
		HashSet<Node> nodes = new HashSet<Node>();
		nodes.add(src);

		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(src);

		while (!queue.isEmpty()) {
			Node current = queue.pop();
			nodes.add(current);
			if (current instanceof DirectedNode) {
				for (IElement out : ((DirectedNode) current).getOutgoingEdges()) {
					Node n = ((DirectedEdge) out).getDst();
					if (n.equals(dst)) {
						return new HashSet<Node>(0);
					}
					if (!nodes.contains(n)) {
						nodes.add(n);
						queue.add(n);
					}
				}
				for (IElement in : ((DirectedNode) current).getIncomingEdges()) {
					Node n = ((DirectedEdge) in).getSrc();
					if (n.equals(dst)) {
						return new HashSet<Node>(0);
					}
					if (!nodes.contains(n)) {
						nodes.add(n);
						queue.add(n);
					}
				}
			} else if (current instanceof UndirectedNode) {
				for (IElement e : ((UndirectedNode) current).getEdges()) {
					Node n = ((UndirectedEdge) e).getDifferingNode(current);
					if (n.equals(dst)) {
						return new HashSet<Node>(0);
					}
					if (!nodes.contains(n)) {
						nodes.add(n);
						queue.add(n);
					}
				}
			}
		}

		return nodes;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof WeakConnectivity;
	}
}
