package dna.metrics.motifs;

import dna.metrics.IMetric;
import dna.metrics.algorithms.IRecomputation;

public class UndirectedMotifsR extends UndirectedMotifs implements
		IRecomputation {

	public UndirectedMotifsR() {
		super("UndirectedMotifsR", IMetric.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
