package dna.depr.metrics.degree;

import dna.depr.metrics.MetricOld;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.distr.IntDistr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

public abstract class DegreeDistribution extends MetricOld {

	protected IntDistr degree;

	protected IntDistr inDegree;

	protected IntDistr outDegree;

	public static final String degreeName = "degreeDistribution";

	public static final String inDegreeName = "inDegreeDistribution";

	public static final String outDegreeName = "outDegreeDistribution";

	protected int nodes;

	protected int edges;

	public static final String nodesName = "nodes";

	public static final String edgesName = "edges";

	public DegreeDistribution(String name, ApplicationType type,
			Metric.MetricType mType) {
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
	public Distr<?>[] getDistributions() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			return new Distr<?>[] { this.degree, this.inDegree, this.outDegree };
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return new Distr<?>[] { this.degree };
		}
		return null;
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m == null || !(m instanceof DegreeDistribution)) {
			return false;
		}
		DegreeDistribution dd = (DegreeDistribution) m;
		boolean success = true;
		success &= DataUtils.equals(this.nodes, dd.nodes, "DD/" + nodesName);
		success &= DataUtils.equals(this.edges, dd.edges, "DD/" + edgesName);
		success &= ArrayUtils.equals(this.degree.getValues(),
				dd.degree.getValues(), "DD/" + degreeName);
		success &= ArrayUtils.equals(this.inDegree.getValues(),
				dd.inDegree.getValues(), "DD/" + inDegreeName);
		success &= ArrayUtils.equals(this.outDegree.getValues(),
				dd.outDegree.getValues(), "DD/" + outDegreeName);
		return success;
	}

	@Override
	public void init_() {
		this.degree = new IntDistr(degreeName, Long.valueOf(this.g
				.getNodeCount()), new long[0]);
		this.inDegree = new IntDistr(inDegreeName, Long.valueOf(this.g
				.getNodeCount()), new long[0]);
		this.outDegree = new IntDistr(outDegreeName, Long.valueOf(this.g
				.getNodeCount()), new long[0]);
		this.nodes = 0;
		this.edges = 0;
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
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
