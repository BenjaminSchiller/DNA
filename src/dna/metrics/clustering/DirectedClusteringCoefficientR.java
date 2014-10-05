package dna.metrics.clustering;

import dna.metrics.IMetric;
import dna.metrics.algorithms.IRecomputation;

public class DirectedClusteringCoefficientR extends
		DirectedClusteringCoefficient implements IRecomputation {

	public DirectedClusteringCoefficientR() {
		super("DirectedClusteringCoefficientR", IMetric.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
