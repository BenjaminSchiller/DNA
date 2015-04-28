package dna.metrics.workload.operations;

import dna.graph.Graph;
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
	public void init(Graph g) {
	}

	@Override
	protected void createWorkloadE(Graph g) {
		g.getRandomEdge();
	}

	@Override
	protected void createWorkloadV(Graph g) {
		g.getRandomNode();
	}

}
