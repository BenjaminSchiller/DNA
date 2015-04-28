package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;

/**
 * operation to iterate over all elements of a list
 * 
 * @author benni
 *
 */
public class Iterate extends Operation {

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 */
	public Iterate(ListType list, int times) {
		super("Iterate", list, times);
	}

	@Override
	public void init(Graph g) {
	}

	@Override
	protected void createWorkloadE(Graph g) {
		Edge edge = null;
		for (IElement e : g.getEdges()) {
			edge = (Edge) e;
		}
	}

	@Override
	protected void createWorkloadV(Graph g) {
		Node node = null;
		for (IElement n : g.getNodes()) {
			node = (Node) n;
		}
	}

}
