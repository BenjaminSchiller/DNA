package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;
import dna.metrics.workload.Operation.ListType;
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

	public AddSuccess(ListType list, int times) {
		super("AddSuccess", list, times);
	}

	@Override
	public void init(Graph g) {

	}

	@Override
	protected void createWorkloadE(Graph g) {
		// TODO Auto-generated method stub
		Log.error("AddSuccess is not implemented for list type E");
	}

	@Override
	protected void createWorkloadV(Graph g) {
		Node node = g.getGraphDatastructures().newNodeInstance(
				g.getMaxNodeIndex() + 1);
		g.addNode(node);
	}

}
