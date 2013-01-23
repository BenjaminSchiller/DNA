package dynamicGraphs.diff.generator;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.util.Rand;

public class RandomDiff {
	public static Diff generate(Graph g, int add, int remove, boolean undirected) {
		return RandomDiff.generate(g, add, remove, undirected,
				g.getTimestamp(), g.getTimestamp() + 1);
	}

	// TODO only remove existing edges
	// TODO only add missing edges
	public static Diff generate(Graph g, int add, int remove,
			boolean undirected, long from, long to) {
		Diff d = new Diff(g.getNodes().length, from, to);

		while (d.getAddedEdges().size() < add) {
			Edge e = Rand.edge(g);
			d.addAddedEdges(e);
			if (undirected) {
				d.addAddedEdges(e.invert());
			}
		}

		while (d.getRemovedEdges().size() < remove) {
			Edge e = Rand.edge(g);
			if (d.addsEdge(e)) {
				continue;
			}
			d.addRemovedEdge(e);
			if (undirected) {
				d.addRemovedEdge(e.invert());
			}
		}

		return d;
	}
}
