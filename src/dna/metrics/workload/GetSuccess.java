package dna.metrics.workload;

import dna.graph.Graph;
import dna.graph.edges.Edge;

public class GetSuccess extends RandomElementWorkload {

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
