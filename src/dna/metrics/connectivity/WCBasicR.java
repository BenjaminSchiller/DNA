package dna.metrics.connectivity;

import dna.metrics.algorithms.IRecomputation;

public class WCBasicR extends WCBasic implements IRecomputation {

	public WCBasicR() {
		super("WCBasicR");
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
