package dna.metrics.richClub;

import dna.metrics.IMetricNew;
import dna.metrics.algorithms.IRecomputation;

public class RichClubConnectivityByDegreeR extends RichClubConnectivityByDegree
		implements IRecomputation {

	public RichClubConnectivityByDegreeR() {
		super("RichClubConnectivityByDegreeR", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute() != null;
	}
}
