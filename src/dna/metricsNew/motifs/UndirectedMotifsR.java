package dna.metricsNew.motifs;

import dna.metrics.Metric.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class UndirectedMotifsR extends UndirectedMotifs implements
		IRecomputation {

	public UndirectedMotifsR() {
		super("UndirectedMotifsR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
