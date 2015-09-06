package dna.metrics.workload.operations;

import dna.graph.IGraph;
import dna.metrics.workload.Operation;

/**
 * operation for retrieving a random element from a list
 * 
 * @author benni
 *
 */
public class RandomElement extends Operation {

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 */
	public RandomElement(ListType list, int times) {
		super("RandomElement", list, times);
	}

	@Override
	protected void createWorkloadE(IGraph g) {
		g.getRandomEdge();
	}

	@Override
	protected void createWorkloadV(IGraph g) {
		g.getRandomNode();
	}

}
