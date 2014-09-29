package dna.metricsNew.clustering;

import dna.metrics.Metric.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class UndirectedClusteringCoefficientR extends
		UndirectedClusteringCoefficient implements IRecomputation {

	public UndirectedClusteringCoefficientR() {
		super("UndirectedClusteringCoefficientR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
