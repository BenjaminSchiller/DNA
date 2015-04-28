package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;
import dna.metrics.workload.Operation.ListType;

public class ContainsFailure extends Operation {

	protected Node node1;

	protected Node node2;

	protected Edge edge;

	public ContainsFailure(ListType list, int times) {
		super("ContainsFailure", list, times);
	}

	@Override
	public void init(Graph g) {
		this.node1 = g.getGraphDatastructures().newNodeInstance(
				Integer.MAX_VALUE);
		this.node2 = g.getGraphDatastructures().newNodeInstance(
				Integer.MAX_VALUE - 1);
		this.edge = g.getGraphDatastructures().newEdgeInstance(this.node1,
				this.node2);
	}

	@Override
	protected void createWorkloadE(Graph g) {
		g.containsEdge(this.edge);
	}

	@Override
	protected void createWorkloadV(Graph g) {
		g.containsNode(this.node1);
	}

}
