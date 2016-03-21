package dna.metrics.streaM_k.groupingWithGroups;

import java.util.HashSet;

import dna.graph.edges.Edge;

public abstract class Grouping2 {

	public HashSet<Group> getGroups(int nodes, Edge e) {
		if (nodes == 3) {
			return getGroups3(e);
		} else if (nodes == 4) {
			return getGroups4(e);
		} else if (nodes == 5) {
			return getGroups5(e);
		} else if (nodes == 6) {
			return getGroups5(e);
		} else {
			throw new IllegalArgumentException("unsupported motif size: "
					+ nodes);
		}
	}

	protected abstract HashSet<Group> getGroups3(Edge e);

	protected abstract HashSet<Group> getGroups4(Edge e);

	protected abstract HashSet<Group> getGroups5(Edge e);

	protected abstract HashSet<Group> getGroups6(Edge e);

}
