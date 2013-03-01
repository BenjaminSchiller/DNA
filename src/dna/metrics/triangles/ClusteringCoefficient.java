package dna.metrics.triangles;

import dna.graph.Graph;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;

public abstract class ClusteringCoefficient extends Metric {
	public ClusteringCoefficient(String key, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		super(key, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
	}

	public String toString() {
		return this.triangleCount + "/" + this.potentialCount + " => "
				+ String.format("%.5f", this.globalCC) + " / "
				+ String.format("%.5f", this.averageCC) + " CC("
				+ this.getName() + ")";
	}

	@Override
	protected void init(Graph g) {
		this.globalCC = -1;
		this.localCC = new double[g.getNodes().length];
		this.averageCC = -1;
		this.nodeTriangleCount = new long[g.getNodes().length];
		this.nodePotentialCount = new long[g.getNodes().length];
	}

	private double globalCC;

	private double[] localCC;

	private double averageCC;

	public double getGlobalCC() {
		return globalCC;
	}

	public double[] getLocalCC() {
		return localCC;
	}

	public double getAverageCC() {
		return averageCC;
	}

	protected long triangleCount;

	protected long potentialCount;

	protected long[] nodeTriangleCount;

	protected long[] nodePotentialCount;

	public long getTriangleCount() {
		return this.triangleCount;
	}

	public long getPotentialCount() {
		return this.potentialCount;
	}

	protected void computeCC() {
		this.globalCC = (double) this.triangleCount
				/ (double) this.potentialCount;
		for (int i = 0; i < this.nodeTriangleCount.length; i++) {
			if (this.nodePotentialCount[i] == 0) {
				this.localCC[i] = 0;
			} else {
				this.localCC[i] = (double) this.nodeTriangleCount[i]
						/ (double) this.nodePotentialCount[i];
			}
		}
		this.averageCC = ArrayUtils.avg(this.localCC);
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof ClusteringCoefficient)) {
			return false;
		}
		ClusteringCoefficient cc = (ClusteringCoefficient) m;
		return this.globalCC == cc.globalCC && this.averageCC == cc.averageCC
				&& ArrayUtils.equals(this.localCC, cc.localCC);
	}

	@Override
	protected boolean compute_() {
		this.triangleCount = 0;
		this.potentialCount = 0;
		for (Node n : this.g.getNodes()) {
			this.nodeTriangleCount[n.getIndex()] = 0;
			this.nodePotentialCount[n.getIndex()] = 0;
			for (Node u : n.getNeighbors()) {
				for (Node v : n.getNeighbors()) {
					if (u.equals(v)) {
						continue;
					}
					this.nodePotentialCount[n.getIndex()]++;
					if (v.hasOut(u)) {
						this.nodeTriangleCount[n.getIndex()]++;
					}
				}
			}
			this.nodeTriangleCount[n.getIndex()] = this.nodeTriangleCount[n
					.getIndex()];
			this.nodePotentialCount[n.getIndex()] = this.nodePotentialCount[n
					.getIndex()];
			this.triangleCount += this.nodeTriangleCount[n.getIndex()];
			this.potentialCount += this.nodePotentialCount[n.getIndex()];
		}
		this.computeCC();
		return true;
	}

	public boolean cleanupApplication() {
		this.computeCC();
		return true;
	}

	public void reset_() {
		this.averageCC = 0;
		this.globalCC = 0;
		this.localCC = null;
		this.triangleCount = 0;
		this.potentialCount = 0;
		this.nodeTriangleCount = null;
		this.nodePotentialCount = null;
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("averageClusteringCoefficient", this.averageCC);
		Value v2 = new Value("globalClusteringCoefficient", this.globalCC);
		Value v3 = new Value("triangleCount", this.triangleCount);
		Value v4 = new Value("potentialTriangleCount", this.potentialCount);
		return new Value[] { v1, v2, v3, v4 };
	}

	@Override
	public Distribution[] getDistributions() {
		// TODO add possibility other than distribution to store values like LCC
		return new Distribution[] {};
	}
}
