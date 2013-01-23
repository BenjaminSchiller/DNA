package dynamicGraphs.util;

import dynamicGraphs.graph.Graph;

public class GraphStats extends Stats {
	public GraphStats(Graph g) {
		super(g.toString());
		this.g = g;
	}

	private Graph g;

	public Graph getGraph() {
		return this.g;
	}
}
