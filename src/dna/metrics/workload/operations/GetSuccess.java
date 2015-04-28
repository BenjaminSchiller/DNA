package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.metrics.workload.Operation;
import dna.metrics.workload.OperationWithRandomSample;
import dna.metrics.workload.Operation.ListType;

public class GetSuccess extends OperationWithRandomSample {

	public GetSuccess(ListType list, int times, int samplesNodes,
			int samplesEdges) {
		super("GetSuccess", list, times, samplesNodes, samplesEdges);
	}

	@Override
	protected void createWorkloadE(Graph g) {
		Edge e = this.getSampleEdge();
		g.getEdge(e.getN1(), e.getN2());
	}

	@Override
	protected void createWorkloadV(Graph g) {
		g.getNode(this.getSampleNode().getIndex());
	}

}
