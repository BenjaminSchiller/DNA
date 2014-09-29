package dna.metricsNew.richClub;

import dna.metricsNew.IMetricNew;
import dna.metricsNew.IMetricNew.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

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
