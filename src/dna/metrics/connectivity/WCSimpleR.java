package dna.metrics.connectivity;

import dna.metrics.algorithms.IRecomputation;

public class WCSimpleR extends WCSimple implements IRecomputation {

	public WCSimpleR() {
		super("WCSimpleR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
