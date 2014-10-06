package dna.metrics.connectivity;

import dna.metrics.algorithms.IRecomputation;

public class StrongConnectivityR extends StrongConnectivity implements
		IRecomputation {

	public StrongConnectivityR() {
		super("StrongConnectivityR");
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
