package dna.metrics.workload;

import dna.graph.Graph;

public class ContainsSuccess extends RandomElementWorkload {

	public ContainsSuccess(ListType list, int times, int nodeSamples,
			int edgeSamples) {
		super("ContainsSuccess", list, times, nodeSamples, edgeSamples);
	}

	@Override
	protected void createWorkloadE(Graph g) {
		g.containsEdge(this.getSampleEdge());
	}

	@Override
	protected void createWorkloadV(Graph g) {
		g.containsNode(this.getSampleNode());
	}

}
