package dna.metrics.workload.operations;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;

/**
 * 
 * operation for executing a contains operation that fails, i.e., the requested
 * element does not exists in the list.
 * 
 * @author benni
 *
 */
public class ContainsFailure extends Operation {

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
	public ContainsFailure(ListType list, int times) {
		super("ContainsFailure", list, times);
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
		g.containsEdge(this.edge);
	}

	@Override
	protected void createWorkloadV(IGraph g) {
		g.containsNode(this.node1);
	}

	@Override
	protected void createWorkloadIn(IGraph g) {
		for (IElement n_ : g.getNodes()) {
			((DirectedNode) n_).hasEdge(this.edge);
		}
	}

	@Override
	protected void createWorkloadOut(IGraph g) {
		for (IElement n_ : g.getNodes()) {
			((DirectedNode) n_).hasEdge(this.edge);
		}
	}

	@Override
	protected void createWorkloadNeighbors(IGraph g) {
		for (IElement n_ : g.getNodes()) {
			((DirectedNode) n_).hasNeighbor((DirectedNode) this.node1);
		}
	}

	@Override
	protected void createWorkloadAdj(IGraph g) {
		for (IElement n_ : g.getNodes()) {
			((Node) n_).hasEdge(this.edge);
		}
	}

}
