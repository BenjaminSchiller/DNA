package dna.metrics.degree;

import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.MetricNew;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

public abstract class DegreeDistribution extends MetricNew {

	protected double[] degreeDistribution;

	protected double[] inDegreeDistribution;

	protected double[] outDegreeDistribution;

	protected double nodes;

	protected double edges;

	public DegreeDistribution(String name, ApplicationType type) {
		super(name, type);
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
		Value nodes = new Value("nodes", this.nodes);
		Value edges = new Value("edges", this.edges);
		return new Value[] { nodes, edges };
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
	public boolean equals(MetricNew m) {
		if (m == null || !(m instanceof DegreeDistribution)) {
			return false;
		}
		DegreeDistribution dd = (DegreeDistribution) m;
		return DataUtils.equals(this.nodes, dd.nodes, "DD/nodes")
				&& DataUtils.equals(this.edges, dd.edges, "DD/edges")
				&& ArrayUtils.equals(this.degreeDistribution,
						dd.degreeDistribution, "DD/degreeDistribution")
				&& ArrayUtils.equals(this.inDegreeDistribution,
						dd.inDegreeDistribution, "DD/inDegreeDistribution")
				&& ArrayUtils.equals(this.outDegreeDistribution,
						dd.outDegreeDistribution, "DD/outDegreeDistribution");
	}

	@Override
	public boolean isComparableTo(MetricNew m) {
		return m != null && m instanceof DegreeDistribution;
	}

}
