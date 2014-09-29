package dna.metricsNew.richClub;

import dna.metrics.Metric.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class RichClubConnectivityByDegreeR extends RichClubConnectivityByDegree
		implements IRecomputation {

	public RichClubConnectivityByDegreeR() {
		super("RichClubConnectivityByDegreeR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute() != null;
	}
}
