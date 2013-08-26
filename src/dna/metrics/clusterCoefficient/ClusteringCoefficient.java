package dna.metrics.clusterCoefficient;

import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

@SuppressWarnings("rawtypes")
public abstract class ClusteringCoefficient extends Metric {

	public ClusteringCoefficient(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
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
	protected void init_() {
		this.globalCC = 0;
		this.averageCC = 0;
		this.localCC = ArrayUtils.init(g.getMaxNodeIndex() + 1, Double.NaN);
		this.triangleCount = 0;
		this.potentialCount = 0;
		this.nodeTriangleCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
				Long.MIN_VALUE);
		this.nodePotentialCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
				Long.MIN_VALUE);
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
	public boolean equals(Metric m) {
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

	protected void addTriangle(Node origin) {
		this.triangleCount++;
		this.nodeTriangleCount[origin.getIndex()]++;
		this.updateNode(origin.getIndex());
	}

	protected void removeTriangle(Node origin) {
		this.triangleCount--;
		this.nodeTriangleCount[origin.getIndex()]--;
		this.updateNode(origin.getIndex());
	}

	protected void addPotentials(Node origin, int count) {
		this.potentialCount += count;
		this.nodePotentialCount[origin.getIndex()] += count;
		this.updateNode(origin.getIndex());
	}

	protected void removePotentials(Node origin, int count) {
		this.potentialCount -= count;
		this.nodePotentialCount[origin.getIndex()] -= count;
		this.updateNode(origin.getIndex());
	}

	protected void updateNode(int index) {
		if (this.nodePotentialCount[index] == 0) {
			this.localCC[index] = 0;
		} else {
			this.localCC[index] = (double) this.nodeTriangleCount[index]
					/ this.nodePotentialCount[index];
		}
		if (this.potentialCount == 0) {
			this.globalCC = 0;
			this.averageCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
			this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC);
		}
	}

}
