package dna.metrics.degree;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public abstract class DegreeDistribution extends Metric {

	protected DistributionInt degree;
	protected DistributionInt inDegree;
	protected DistributionInt outDegree;

	public DegreeDistribution(String name, Parameter... p) {
		super(name, p);
	}

	@Override
	public Value[] getValues() {
		Value degree_average = new Value("degree_average", degree.computeAverage());
		Value degree_maxValue = new Value("degree_maxValue", degree.getMax());
		Value degree_minValue = new Value("degree_minValue", degree.getMin());
		Value indegree_average = new Value("indegree_average", inDegree.computeAverage());
		Value indegree_maxValue = new Value("indegree_maxValue", inDegree.getMax());
		Value indegree_minValue = new Value("indegree_minValue", inDegree.getMin());
		Value outdegree_average = new Value("outdegree_average", outDegree.computeAverage());
		Value outdegree_maxValue = new Value("outdegree_maxValue", outDegree.getMax());
		Value outdegree_minValue = new Value("outdegree_minValue",outDegree.getMin());
		return new Value[]{degree_average,degree_maxValue,degree_minValue,indegree_average,indegree_maxValue,indegree_minValue,outdegree_average,outdegree_maxValue,outdegree_minValue};
	}

	@Override
	public Distribution[] getDistributions() {
		if (this.g.isDirected()) {
			return new Distribution[] { this.degree, this.inDegree,
					this.outDegree };
		} else {
			return new Distribution[] { this.degree };
		}
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[0];
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m instanceof DegreeDistribution;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof DegreeDistribution)) {
			return false;
		}
		DegreeDistribution dd = (DegreeDistribution) m;
		boolean equals = true;
		equals &= ArrayUtils.equals(this.degree.getIntValues(),
				dd.degree.getIntValues(), this.degree.getName());
		if (this.inDegree != null) {
			equals &= ArrayUtils.equals(this.inDegree.getIntValues(),
					dd.inDegree.getIntValues(), this.inDegree.getName());
			equals &= ArrayUtils.equals(this.outDegree.getIntValues(),
					dd.outDegree.getIntValues(), this.outDegree.getName());
		}
		return equals;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	protected boolean compute() {
		if (this.g.isDirected()) {
			this.degree = new DistributionInt("DegreeDistribution");
			this.inDegree = new DistributionInt("InDegreeDistribution");
			this.outDegree = new DistributionInt("OutDegreeDistribution");
			for (IElement n_ : this.g.getNodes()) {
				DirectedNode n = (DirectedNode) n_;
				this.degree.incr(n.getDegree());
				this.inDegree.incr(n.getInDegree());
				this.outDegree.incr(n.getOutDegree());
			}
		} else {
			this.degree = new DistributionInt("DegreeDistribution");
			this.inDegree = null;
			this.outDegree = null;
			for (IElement n_ : this.g.getNodes()) {
				UndirectedNode n = (UndirectedNode) n_;
				this.degree.incr(n.getDegree());
			}
		}
		return true;
	}

}
