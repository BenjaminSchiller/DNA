package dna.metrics.clusterCoefficient;

import dna.graph.Graph;
import dna.metrics.MetricNew;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

@SuppressWarnings("rawtypes")
public abstract class ClusteringCoefficient extends MetricNew {

	public ClusteringCoefficient(String name, ApplicationType type) {
		super(name, type);
	}

	protected double globalCC;

	protected double averageCC;

	// TODO need a datastructure to store localCC (!= Distribution)
	protected double[] localCC;

	protected long triangleCount;

	protected long potentialCount;

	protected long[] nodeTriangleCount;

	protected long[] nodePotentialCount;

	@Override
	protected void init_(Graph g) {
		this.globalCC = 0;
		this.averageCC = 0;
		this.localCC = new double[g.getNodeCount()];
		this.triangleCount = 0;
		this.potentialCount = 0;
		this.nodeTriangleCount = new long[g.getNodeCount()];
		this.nodePotentialCount = new long[g.getNodeCount()];
	}

	@Override
	public void reset_() {
		this.globalCC = 0;
		this.averageCC = 0;
		this.localCC = null;
		this.triangleCount = 0;
		this.potentialCount = 0;
		this.nodeTriangleCount = null;
		this.nodePotentialCount = null;
	}

	@Override
	protected Value[] getValues() {
		Value globalCC = new Value("globalCC", this.globalCC);
		Value averageCC = new Value("averageCC", this.averageCC);
		return new Value[] { globalCC, averageCC };
	}

	@Override
	protected Distribution[] getDistributions() {
		return new Distribution[0];
	}

	@Override
	public boolean equals(MetricNew m) {
		if (m == null || !(m instanceof ClusteringCoefficient)) {
			return false;
		}
		ClusteringCoefficient cc = (ClusteringCoefficient) m;
		boolean success = true;
		success &= DataUtils.equals(this.globalCC, cc.globalCC, "globalCC");
		success &= DataUtils.equals(this.averageCC, cc.averageCC, "averageCC");
		success &= ArrayUtils.equals(this.localCC, cc.localCC, "localCC");
		success &= DataUtils.equals(this.triangleCount, cc.triangleCount,
				"triangleCount");
		success &= DataUtils.equals(this.potentialCount, cc.potentialCount,
				"potentialCount");
		success &= ArrayUtils.equals(this.nodeTriangleCount,
				cc.nodeTriangleCount, "nodeTriangleCount");
		success &= ArrayUtils.equals(this.nodePotentialCount,
				cc.nodePotentialCount, "nodePotentialCount");
		return success;
	}

	@Override
	public boolean isComparableTo(MetricNew m) {
		return m != null && m instanceof ClusteringCoefficient;
	}

}
