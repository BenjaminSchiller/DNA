package dna.metrics.workload;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class GetFailure extends Operation {

	protected Node node1;

	protected Node node2;

	protected Edge edge;

	public GetFailure(ListType list, int times) {
		super("GetFailure", list, times);
	}

	@Override
	protected void init(Graph g) {
		this.node1 = g.getGraphDatastructures().newNodeInstance(
				Integer.MAX_VALUE);
		this.node2 = g.getGraphDatastructures().newNodeInstance(
				Integer.MAX_VALUE - 1);
		this.edge = g.getGraphDatastructures().newEdgeInstance(this.node1,
				this.node2);
	}

	@Override
	protected void createWorkloadE(Graph g) {
		g.getEdge(this.node1, this.node2);
	}

	@Override
	protected void createWorkloadV(Graph g) {
		g.getNode(this.node1.getIndex());
	}

}
