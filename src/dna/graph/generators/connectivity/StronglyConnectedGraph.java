package dna.graph.generators.connectivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.util.Log;

public class StronglyConnectedGraph extends ConnectedGraph {

	public StronglyConnectedGraph(GraphGenerator gg) {
		super("StronglyConnectedComponent", gg);
	}

	@Override
	protected Set<Node> getNodesToExclude(Graph g) {
		HashSet<Node> processed = new HashSet<Node>();
		ArrayList<HashSet<Node>> components = new ArrayList<HashSet<Node>>();

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			if (processed.contains(n)) {
				continue;
			}
			processed.add(n);

			HashSet<Node> component = new HashSet<Node>();
			components.add(component);
			component.add(n);

			LinkedList<Node> stack_forward = new LinkedList<Node>();
			stack_forward.addLast(n);
			HashSet<Node> processed_forward = new HashSet<Node>();
			processed_forward.add(n);

			while (!stack_forward.isEmpty()) {
				DirectedNode current = (DirectedNode) stack_forward.pollFirst();
				for (IElement e_ : current.getOutgoingEdges()) {
					Node n2 = ((DirectedEdge) e_).getDst();
					if (processed_forward.contains(n2)) {
						continue;
					}

					stack_forward.addLast(n2);
					processed_forward.add(n2);
				}
			}

			LinkedList<Node> stack_backward = new LinkedList<Node>();
			stack_backward.addLast(n);
			HashSet<Node> processed_backward = new HashSet<Node>();
			processed_backward.add(n);

			while (!stack_backward.isEmpty()) {
				DirectedNode current = (DirectedNode) stack_backward
						.pollFirst();
				for (IElement e_ : current.getIncomingEdges()) {
					Node n2 = ((DirectedEdge) e_).getSrc();
					if (processed_backward.contains(n2)) {
						continue;
					}

					stack_backward.addLast(n2);
					processed_backward.add(n2);

					if (processed_forward.contains(n2)) {
						processed.add(n2);
						component.add(n2);
					}
				}
			}
		}

		int maxIndex = 0;
		for (int i = 1; i < components.size(); i++) {
			if (components.get(i).size() > components.get(maxIndex).size()) {
				maxIndex = i;
			}
		}

		HashSet<Node> exclude = new HashSet<Node>();
		for (int i = 0; i < components.size(); i++) {
			if (i != maxIndex) {
				exclude.addAll(components.get(i));
			}
		}

		Log.debug("components: " + components.size());
		Log.debug("max: " + components.get(maxIndex).size() + " @ " + maxIndex);
		Log.debug("exclude: " + exclude.size() + " nodes");
		// for (int i = 0; i < components.size(); i++) {
		// Log.debug(i + ": " + components.get(i).size());
		// Log.debug("   => " + components.get(i));
		// }

		return exclude;
	}

}
