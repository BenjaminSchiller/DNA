package dna.depr.metrics.sampling;

import dna.graph.Graph;
import dna.metrics.IMetricNew.MetricType;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * This metric will measure the fraction between sample and original graph. It
 * will generate two values: SamplingModularityV1 and SamplingModularityV2. SMV1
 * compares the amount of edges in the sample with the amount of edges in the
 * original graph. SMV2 compares the amount of edges between sampled and not
 * sampled nodes, with the amount of edges in the original graph.
 * 
 * @author Benedict Jahn
 * 
 */
public class SamplingModularityR extends SamplingModularity {

	/**
	 * Creates an instance of the sampling modularity metric, which gets
	 * completely recomputed with every batch.
	 * 
	 * @param fullGraph
	 *            the original full graph
	 */
	public SamplingModularityR(Graph fullGraph) {
		super("SamplingModularityR", ApplicationType.Recomputation,
				MetricType.exact, fullGraph);
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
