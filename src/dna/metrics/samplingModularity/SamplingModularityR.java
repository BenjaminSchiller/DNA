package dna.metrics.samplingModularity;

import dna.graph.Graph;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * @author Benedict Jahn
 * 
 */
public class SamplingModularityR extends SamplingModularity {

	/**
	 * 
	 */
	public SamplingModularityR(Graph fullGraph) {
		super("SamplingModularityR", ApplicationType.Recomputation, MetricType.exact, fullGraph);
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
