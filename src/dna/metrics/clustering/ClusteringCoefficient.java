package dna.metrics.clustering;

import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.lists.LongList;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;
import dna.util.DataUtils;
import dna.util.parameters.Parameter;

public abstract class ClusteringCoefficient extends Metric {

	public double globalCC;

	public double averageCC;

	public NodeValueList localCC;

	public long triangleCount;

	public long potentialCount;

	public LongList nodeTriangleCount;

	public LongList nodePotentialCount;

	public ClusteringCoefficient(String name, Parameter... p) {
		super(name, MetricType.exact, p);
	}

	public ClusteringCoefficient(String name, String[] nodeTypes,
			Parameter... p) {
		super(name, MetricType.exact, nodeTypes, p);
	}

	@Override
	public Value[] getValues() {
		Value globalCC = new Value("globalCC", this.globalCC);
		Value averageCC = new Value("averageCC", this.averageCC);
		Value triangleCount = new Value("triangleCount", this.triangleCount);
		Value potentialCount = new Value("potentialCount", this.potentialCount);
		return new Value[] { globalCC, averageCC, triangleCount, potentialCount };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		NodeValueList nodeTriangleCount = new NodeValueList(
				"nodeTriangleCount", this.nodeTriangleCount.getValues());
		NodeValueList nodePotentialCount = new NodeValueList(
				"nodePotentialCount", this.nodePotentialCount.getValues());
		return new NodeValueList[] { this.localCC, nodeTriangleCount,
				nodePotentialCount };
		// return new NodeValueList[] { this.localCC };
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof ClusteringCoefficient;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof ClusteringCoefficient)) {
			return false;
		}
		ClusteringCoefficient cc = (ClusteringCoefficient) m;
		boolean success = true;
		success &= DataUtils.equals(this.globalCC, cc.globalCC, "globalCC");
		success &= DataUtils.equals(this.averageCC, cc.averageCC, "averageCC");
		success &= ArrayUtils.equals(this.localCC.getValues(),
				cc.localCC.getValues(), "localCC");
		success &= DataUtils.equals(this.triangleCount, cc.triangleCount,
				"triangleCount");
		success &= DataUtils.equals(this.potentialCount, cc.potentialCount,
				"potentialCount");
		// success &= ArrayUtils.equals(this.nodeTriangleCount.getValues(),
		// cc.nodeTriangleCount.getValues(), "nodeTriangleCount");
		// success &= ArrayUtils.equals(this.nodePotentialCount.getValues(),
		// cc.nodePotentialCount.getValues(), "nodePotentialCount");
		return success;
	}

	protected void addTriangle(Node origin) {
		if (!this.isNodeOfAssignedType(origin)) {
			return;
		}
		this.triangleCount++;
		this.nodeTriangleCount.incr(origin.getIndex());
		this.updateNode(origin.getIndex());
	}

	protected void removeTriangle(Node origin) {
		if (!this.isNodeOfAssignedType(origin)) {
			return;
		}
		this.triangleCount--;
		this.nodeTriangleCount.decr(origin.getIndex());
		this.updateNode(origin.getIndex());
	}

	protected void addPotentials(Node origin, int count) {
		if (!this.isNodeOfAssignedType(origin)) {
			return;
		}
		this.potentialCount += count;
		this.nodePotentialCount.add(origin.getIndex(), count);
		this.updateNode(origin.getIndex());
	}

	protected void removePotentials(Node origin, int count) {
		if (!this.isNodeOfAssignedType(origin)) {
			return;
		}
		this.potentialCount -= count;
		this.nodePotentialCount.sub(origin.getIndex(), count);
		this.updateNode(origin.getIndex());
	}

	protected void updateNode(int index) {
		if (this.nodePotentialCount.getValue(index) == 0) {
			this.localCC.setValue(index, 0);
		} else {
			this.localCC.setValue(index,
					(double) this.nodeTriangleCount.getValue(index)
							/ this.nodePotentialCount.getValue(index));
		}
		if (this.potentialCount == 0) {
			this.globalCC = 0;
			this.averageCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
			this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());
		}
	}

}
