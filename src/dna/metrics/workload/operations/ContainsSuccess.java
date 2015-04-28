package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.metrics.workload.Operation;
import dna.metrics.workload.OperationWithRandomSample;
import dna.metrics.workload.Operation.ListType;

public class ContainsSuccess extends OperationWithRandomSample {

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
