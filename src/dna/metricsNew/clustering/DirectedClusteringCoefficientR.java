package dna.metricsNew.clustering;

import dna.metrics.Metric.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class DirectedClusteringCoefficientR extends
		DirectedClusteringCoefficient implements IRecomputation {

	public DirectedClusteringCoefficientR() {
		super("DirectedClusteringCoefficientR", MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
