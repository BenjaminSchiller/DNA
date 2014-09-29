package dna.metricsNew.clustering;

import dna.metricsNew.IMetricNew;
import dna.metricsNew.IMetricNew.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class DirectedClusteringCoefficientR extends
		DirectedClusteringCoefficient implements IRecomputation {

	public DirectedClusteringCoefficientR() {
		super("DirectedClusteringCoefficientR", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
