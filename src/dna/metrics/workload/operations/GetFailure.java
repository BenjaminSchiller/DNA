package dna.metrics.workload.operations;

import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;

/**
 * operation for performing failing get operations, i.e., the requested element
 * is not contained in the list.
 * 
 * @author benni
 *
 */
public class GetFailure extends Operation {

	protected Node node1;

	protected Node node2;

	protected Edge edge;

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 */
	public GetFailure(ListType list, int times) {
		super("GetFailure", list, times);
	}

	@Override
	public void init(IGraph g) {
		this.node1 = g.getGraphDatastructures().newNodeInstance(
				Integer.MAX_VALUE);
		this.node2 = g.getGraphDatastructures().newNodeInstance(
				Integer.MAX_VALUE - 1);
		this.edge = g.getGraphDatastructures().newEdgeInstance(this.node1,
				this.node2);
	}

	@Override
	protected void createWorkloadE(IGraph g) {
		g.getEdge(this.node1, this.node2);
	}

	@Override
	protected void createWorkloadV(IGraph g) {
		g.getNode(this.node1.getIndex());
	}

}
