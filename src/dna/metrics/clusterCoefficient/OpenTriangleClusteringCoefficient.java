package dna.metrics.clusterCoefficient;

import dna.metrics.Metric;

public abstract class OpenTriangleClusteringCoefficient extends
		ClusteringCoefficient {

	public OpenTriangleClusteringCoefficient(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof OpenTriangleClusteringCoefficient;
	}

}
