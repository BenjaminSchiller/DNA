package dna.depr.metrics.degree;

import dna.depr.metrics.Metric;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metricsNew.MetricNew;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

public abstract class DegreeDistribution extends Metric {

	protected DistributionInt degree;

	protected DistributionInt inDegree;

	protected DistributionInt outDegree;

	public static final String degreeName = "degreeDistribution";

	public static final String inDegreeName = "inDegreeDistribution";

	public static final String outDegreeName = "outDegreeDistribution";

	protected int nodes;

	protected int edges;

	public static final String nodesName = "nodes";

	public static final String edgesName = "edges";

	public DegreeDistribution(String name, ApplicationType type,
			MetricNew.MetricType mType) {
		super(name, type, mType);
	}

	@Override
	public void reset_() {
		this.degree = null;
		this.inDegree = null;
		this.outDegree = null;
		this.nodes = 0;
		this.edges = 0;
	}

	@Override
	public Value[] getValues() {
		return new Value[] { new Value("NODES", this.nodes),
				new Value("EDGES", this.edges) };
	}

	@Override
	public Distribution[] getDistributions() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			return new Distribution[] { this.degree, this.inDegree,
					this.outDegree };
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return new Distribution[] { this.degree };
		}
		return null;
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof DegreeDistribution)) {
			return false;
		}
		DegreeDistribution dd = (DegreeDistribution) m;
		boolean success = true;
		success &= DataUtils.equals(this.nodes, dd.nodes, "DD/" + nodesName);
		success &= DataUtils.equals(this.edges, dd.edges, "DD/" + edgesName);
		success &= ArrayUtils.equals(this.degree.getIntValues(),
				dd.degree.getIntValues(), "DD/" + degreeName);
		success &= ArrayUtils.equals(this.inDegree.getIntValues(),
				dd.inDegree.getIntValues(), "DD/" + inDegreeName);
		success &= ArrayUtils.equals(this.outDegree.getIntValues(),
				dd.outDegree.getIntValues(), "DD/" + outDegreeName);
		return success;
	}

	@Override
	public void init_() {
		this.degree = new DistributionInt(degreeName, new int[0],
				this.g.getNodeCount());
		this.inDegree = new DistributionInt(inDegreeName, new int[0],
				this.g.getNodeCount());
		this.outDegree = new DistributionInt(outDegreeName, new int[0],
				this.g.getNodeCount());
		this.nodes = 0;
		this.edges = 0;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof DegreeDistribution;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[0];
	}

}
