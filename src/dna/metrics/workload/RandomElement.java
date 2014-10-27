package dna.metrics.workload;

import dna.graph.Graph;

public class RandomElement extends Operation {

	public RandomElement(ListType list, int times) {
		super("RandomElement", list, times);
	}

	@Override
	protected void init(Graph g) {
	}

	@Override
	protected void createWorkloadE(Graph g) {
		g.getRandomEdge();
	}

	@Override
	protected void createWorkloadV(Graph g) {
		g.getRandomNode();
	}

}
