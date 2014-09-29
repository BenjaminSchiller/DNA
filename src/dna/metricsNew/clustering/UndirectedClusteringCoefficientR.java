package dna.metricsNew.clustering;

import dna.metricsNew.IMetricNew;
import dna.metricsNew.IMetricNew.MetricType;
import dna.metricsNew.algorithms.IRecomputation;

public class UndirectedClusteringCoefficientR extends
		UndirectedClusteringCoefficient implements IRecomputation {

	public UndirectedClusteringCoefficientR() {
		super("UndirectedClusteringCoefficientR", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
