package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.metrics.workload.OperationWithRandomSample;

/**
 * operation for performing successful get operations, i.e., the requested
 * element is contained in the list.
 * 
 * @author benni
 *
 */
public class GetSuccess extends OperationWithRandomSample {

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 * @param samples
	 *            samples to draw from the list
	 */
	public GetSuccess(ListType list, int times, int samples) {
		super("GetSuccess", list, times, samples);
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
