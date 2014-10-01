package dna.metrics.weights;

import dna.metrics.algorithms.IRecomputation;

public class EdgeWeightsR extends EdgeWeights implements IRecomputation {

	public EdgeWeightsR(double binSize) {
		super("EdgeWeightsR", MetricType.exact, binSize);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
