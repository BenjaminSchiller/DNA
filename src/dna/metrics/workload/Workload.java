package dna.metrics.workload;

import dna.graph.Graph;
import dna.util.Timer;

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
	 * respective number of times) and return the total runtime (without
	 * initialization time).
	 * 
	 * @param g
	 *            graph
	 * @return total runtime of executing the workload
	 */
	public long createWorkload(Graph g) {
		long duration = 0;
		for (Operation o : this.operations) {
			System.out.println("creating workload: " + o.getName());
			Timer t = o.createWorkload(g);
			duration += t.getDutation();
		}
		return duration;
	}

	/**
	 * 
	 * @return number of rouns this workflow should be executed consecutively.
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
