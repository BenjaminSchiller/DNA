package dna.metrics.sampling;

import dna.graph.Graph;
import dna.metrics.algorithms.IRecomputation;

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
public class SamplingModularityR extends SamplingModularity implements
		IRecomputation {

	public SamplingModularityR(Graph graph) {
		super("SamplingModularityR", MetricType.exact, graph);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
