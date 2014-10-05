package dna.metrics.richClub;

import dna.metrics.IMetric;
import dna.metrics.algorithms.IRecomputation;

public class RichClubConnectivityByDegreeR extends RichClubConnectivityByDegree
		implements IRecomputation {

	public RichClubConnectivityByDegreeR() {
		super("RichClubConnectivityByDegreeR", IMetric.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute() != null;
	}
}
