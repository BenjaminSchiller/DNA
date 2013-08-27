package dna.metrics.clusterCoefficient;

import dna.graph.Graph;
import dna.graph.directed.DirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class OpenTriangleClusteringCoefficient extends
		ClusteringCoefficient {

	public OpenTriangleClusteringCoefficient(String name, ApplicationType type,
			MetricType mType) {
		super(name, type, mType);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof OpenTriangleClusteringCoefficient;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
