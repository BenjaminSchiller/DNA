package dna.metrics.clustering;

import dna.metrics.IMetricNew;
import dna.metrics.algorithms.IRecomputation;

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
