package dynamicGraphs.metrics.triangles;

import dynamicGraphs.graph.Graph;
import dynamicGraphs.metrics.Metric;
import dynamicGraphs.util.ArrayUtils;

public abstract class ClusteringCoefficient extends Metric {
	public ClusteringCoefficient(Graph g, String key,
			boolean appliedBeforeDiff, boolean appliedAfterEdge,
			boolean appliedAfterDiff) {
		super(g, key, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
		this.globalCC = -1;
		this.localCC = new double[g.getNodes().length];
		this.averageCC = -1;
	}

	protected double globalCC;

	protected double[] localCC;

	protected double averageCC;

	public double getGlobalCC() {
		return globalCC;
	}

	public double[] getLocalCC() {
		return localCC;
	}

	public double getAverageCC() {
		return averageCC;
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof ClusteringCoefficient)) {
			return false;
		}
		ClusteringCoefficient ot = (ClusteringCoefficient) m;
		return this.globalCC == ot.globalCC && this.averageCC == ot.averageCC
				&& ArrayUtils.equals(this.localCC, ot.localCC);
	}
}
