package dna.graph.generators.connectivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.Log;

public class WeaklyConnectedGraph extends ConnectedGraph {

	public WeaklyConnectedGraph(GraphGenerator gg) {
		super("WeaklyConnectedComponentOf", gg);
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
			HashSet<Node> component = new HashSet<Node>();
			components.add(component);
			LinkedList<Node> stack = new LinkedList<Node>();

			stack.addLast(n);
			processed.add(n);
			component.add(n);

			while (!stack.isEmpty()) {
				Node current = stack.pollFirst();
				for (IElement e_ : current.getEdges()) {
					Edge e = (Edge) e_;
					Node n2 = e.getDifferingNode(current);
					if (processed.contains(n2)) {
						continue;
					}

					stack.addLast(n2);
					processed.add(n2);
					component.add(n2);
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

	protected void process(Node n, HashSet<Node> component,
			HashSet<Node> processed) {
		System.out.println("processing: " + n);
		component.add(n);
		processed.add(n);
		for (IElement e_ : n.getEdges()) {
			Edge e = (Edge) e_;
			Node n2 = e.getDifferingNode(n);
			if (processed.contains(n2)) {
				continue;
			}
			process(n2, component, processed);
		}
	}

}
