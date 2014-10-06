package dna.metrics.weights;

import dna.metrics.algorithms.IRecomputation;

public class NodeWeightsR extends NodeWeights implements IRecomputation {

	public NodeWeightsR(double binSize) {
		super("NodeWeightsR", binSize);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
