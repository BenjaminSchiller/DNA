package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;
import dna.util.Log;

/**
 * 
 * adds a new node / edge to the graph that does not exist yet (hence
 * successful)
 * 
 * @author benni
 * 
 */
public class AddSuccess extends Operation {

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 */
	public AddSuccess(ListType list, int times) {
		super("AddSuccess", list, times);
	}

	@Override
	public void init(Graph g) {

	}

	@Override
	protected void createWorkloadE(Graph g) {
		Log.error("AddSuccess is not implemented for list type E");
	}

	@Override
	protected void createWorkloadV(Graph g) {
		Node node = g.getGraphDatastructures().newNodeInstance(
				g.getMaxNodeIndex() + 1);
		g.addNode(node);
	}

}
