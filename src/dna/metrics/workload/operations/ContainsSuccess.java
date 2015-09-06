package dna.metrics.workload.operations;

import dna.graph.IGraph;
import dna.metrics.workload.OperationWithRandomSample;

/**
 * 
 * operation for executing a contains operation that is successful, i.e., the
 * requested element exists in the list.
 * 
 * @author benni
 *
 */
public class ContainsSuccess extends OperationWithRandomSample {

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 * @param samples
	 *            samples to draw from the list
	 */
	public ContainsSuccess(ListType list, int times, int samples) {
		super("ContainsSuccess", list, times, samples);
	}

	@Override
	protected void createWorkloadE(IGraph g) {
		g.containsEdge(this.getSampleEdge());
	}

	@Override
	protected void createWorkloadV(IGraph g) {
		g.containsNode(this.getSampleNode());
	}

}
