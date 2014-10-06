package dna.metrics.connectivity;

import dna.metrics.algorithms.IRecomputation;

public class WeakConnectivityR extends WeakConnectivity implements
		IRecomputation {

	public WeakConnectivityR() {
		super("WeakConnectivityR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
