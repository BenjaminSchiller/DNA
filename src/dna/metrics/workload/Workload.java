package dna.metrics.workload;

import dna.graph.Graph;

/**
 * 
 * a workload is defined by a list of operations. as part of the WorkloadMetric,
 * a workload is executed for workload.round batches before processing the next
 * workload.
 * 
 * @author benni
 *
 */
public class Workload {
	private int rounds;

	private Operation[] operations;

	public Workload(int rounds, Operation... operations) {
		this.rounds = rounds;
		this.operations = operations;
	}

	/**
	 * create the workload for a given graph, i.e., executes each operation (the
	 * respective number of times) and return the total runtime.
	 * 
	 * @param g
	 *            graph
	 */
	public void createWorkload(Graph g) {
		for (Operation o : this.operations) {
			o.createWorkload(g);
		}
	}

	/**
	 * initializes all operations, this must be called before the workload is
	 * created!
	 * 
	 * @param g
	 *            g
	 */
	public void init(Graph g) {
		for (Operation o : this.operations) {
			o.init(g);
		}
	}

	/**
	 * 
	 * @return number of rounds this workflow should be executed consecutively.
	 */
	public int getRounds() {
		return rounds;
	}

	/**
	 * 
	 * @return the list of operations this workflow consists of
	 */
	public Operation[] getOperations() {
		return operations;
	}
}
