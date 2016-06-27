package dna.metrics.richClub;

import dna.metrics.algorithms.IRecomputation;

public class RichClubCoefficientR extends RichClubCoefficient implements
		IRecomputation {

	public RichClubCoefficientR(int k) {
		super("RichClubCoefficientR", k);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}
}
