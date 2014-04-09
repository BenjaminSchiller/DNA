package dna.metrics.extent;

import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.updates.walkingAlgorithms.WalkingAlgorithm;

/**
 * This metric will compute the number of seen, unseen and visited nodes in a
 * graph which is sampled by an walking algorithm
 * 
 * @author Benedict Jahn
 * 
 */
public class ExtentR extends Extent {

	/**
	 * Creates an instance of the Extent metric
	 * 
	 * @param name
	 *            the name of the metric
	 * @param algorithm
	 *            the walking algorithm, which walks the graph
	 */
	public ExtentR(WalkingAlgorithm algorithm) {
		super("ExtentR", ApplicationType.Recomputation, MetricType.exact, algorithm);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
