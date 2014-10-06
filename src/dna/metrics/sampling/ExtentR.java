package dna.metrics.sampling;

import dna.metrics.algorithms.IRecomputation;
import dna.updates.generators.sampling.SamplingAlgorithm;

/**
 * This metric will measure to which extent a sampling algorithm has sampled the
 * graph. It will compute the number of seen, unseen and visited nodes in a
 * graph.
 * 
 * @author Benedict Jahn
 */
public class ExtentR extends Extent implements IRecomputation {

	public ExtentR(SamplingAlgorithm algorithm) {
		super("ExtentR", algorithm);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
