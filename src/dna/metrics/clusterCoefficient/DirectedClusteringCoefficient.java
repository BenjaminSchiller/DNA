package dna.metrics.clusterCoefficient;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.updates.batch.Batch;

public abstract class DirectedClusteringCoefficient extends
		ClusteringCoefficient {

	public DirectedClusteringCoefficient(String name, ApplicationType type,
			MetricType mType) {
		super(name, type, mType);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof DirectedClusteringCoefficient;
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
