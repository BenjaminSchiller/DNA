package dna.metrics.connectivity;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metricsNew.MetricNew;
import dna.util.parameters.Parameter;

/**
 * 
 * Abstract super class for stron connectivity. Nodes a,b belong to a strongly
 * connected component in case there is a path (a->...->b) as well as a path
 * (b->...->a). For unsidrected graphs, strongly connected components are exacly
 * the same as weakly connected components.
 * 
 * @author benni
 * 
 */
public abstract class StrongConnectivity extends Connectivity {

	public StrongConnectivity(String name, ApplicationType type,
			MetricNew.MetricType metricType, Parameter... p) {
		super(name, type, metricType, p);
	}

	@Override
	protected Collection<Node> getConnectedNodes(Node node) {
		HashSet<Node> nodes = new HashSet<Node>();
		nodes.add(node);

		if (node instanceof DirectedNode) {

			HashSet<Node> forward = new HashSet<Node>();
			LinkedList<Node> queue = new LinkedList<Node>();
			queue.add(node);

			while (!queue.isEmpty()) {
				Node current = queue.pop();
				for (IElement out : ((DirectedNode) current).getOutgoingEdges()) {
					Node n = ((DirectedEdge) out).getDst();
					if (!forward.contains(n)) {
						forward.add(n);
						queue.add(n);
					}
				}
			}

			HashSet<Node> backwards = new HashSet<Node>();
			queue.add(node);

			while (!queue.isEmpty()) {
				Node current = queue.pop();
				if (forward.contains(current)) {
					nodes.add(current);
				}
				for (IElement in : ((DirectedNode) current).getIncomingEdges()) {
					Node n = ((DirectedEdge) in).getSrc();
					if (!backwards.contains(n)) {
						backwards.add(n);
						queue.add(n);
					}
				}
			}

		} else if (node instanceof UndirectedNode) {

			LinkedList<Node> queue = new LinkedList<Node>();
			queue.add(node);

			while (!queue.isEmpty()) {
				Node current = queue.pop();
				nodes.add(current);
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

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof StrongConnectivity;
	}

}
