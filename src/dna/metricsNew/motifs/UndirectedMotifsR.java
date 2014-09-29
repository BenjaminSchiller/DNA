package dna.metricsNew.motifs;

import dna.metricsNew.IMetricNew;
import dna.metricsNew.IMetricNew.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class UndirectedMotifsR extends UndirectedMotifs implements
		IRecomputation {

	public UndirectedMotifsR() {
		super("UndirectedMotifsR", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
