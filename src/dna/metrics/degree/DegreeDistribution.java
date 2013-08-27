package dna.metrics.degree;

import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

public abstract class DegreeDistribution extends Metric {

	protected double[] degreeDistribution;

	protected double[] inDegreeDistribution;

	protected double[] outDegreeDistribution;

	protected double nodes;

	protected double edges;

	public DegreeDistribution(String name, ApplicationType type, MetricType mType) {
		super(name, type, mType);
	}

	@Override
	public void reset_() {
		this.degreeDistribution = null;
		this.inDegreeDistribution = null;
		this.outDegreeDistribution = null;
		this.nodes = 0;
		this.edges = 0;
	}

	@Override
	protected Value[] getValues() {
		return new Value[0];
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution degree = new Distribution("degreeDistribution",
				this.degreeDistribution);
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			Distribution inDegree = new Distribution("inDegreeDistribution",
					this.inDegreeDistribution);
			Distribution outDegree = new Distribution("outDegreeDistribution",
					this.outDegreeDistribution);
			return new Distribution[] { degree, inDegree, outDegree };
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return new Distribution[] { degree };
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
		success &= DataUtils.equals(this.nodes, dd.nodes, "DD/nodes");
		success &= DataUtils.equals(this.edges, dd.edges, "DD/edges");
		success &= ArrayUtils.equals(this.degreeDistribution,
				dd.degreeDistribution, "DD/degreeDistribution");
		success &= ArrayUtils.equals(this.inDegreeDistribution,
				dd.inDegreeDistribution, "DD/inDegreeDistribution");
		success &= ArrayUtils.equals(this.outDegreeDistribution,
				dd.outDegreeDistribution, "DD/outDegreeDistribution");
		return success;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof DegreeDistribution;
	}

}
