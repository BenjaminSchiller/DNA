package dna.metrics.motifs;

import dna.metrics.IMetricNew;
import dna.metrics.algorithms.IRecomputation;

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
