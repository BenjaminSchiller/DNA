package dna.metrics.clustering;

import dna.metrics.IMetric;
import dna.metrics.algorithms.IRecomputation;

public class UndirectedClusteringCoefficientR extends
		UndirectedClusteringCoefficient implements IRecomputation {

	public UndirectedClusteringCoefficientR() {
		super("UndirectedClusteringCoefficientR", IMetric.MetricType.exact);
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

}
