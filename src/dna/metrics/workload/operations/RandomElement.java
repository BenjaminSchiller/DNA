package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.metrics.workload.Operation;
import dna.metrics.workload.Operation.ListType;

public class RandomElement extends Operation {

	public RandomElement(ListType list, int times) {
		super("RandomElement", list, times);
	}

	@Override
	public void init(Graph g) {
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
