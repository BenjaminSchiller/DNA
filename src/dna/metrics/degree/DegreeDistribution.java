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

	public DegreeDistribution(String name, IMetric.MetricType metricType,
			Parameter... p) {
		super(name, metricType, p);
	}

	@Override
	public Value[] getValues() {
		return new Value[0];
	}

	@Override
	public Distribution[] getDistributions() {
		if (this.inDegree != null) {
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
		if (this.g.getGraphDatastructures().isNodeType(UndirectedNode.class)) {
			this.degree = new DistributionInt("DegreeDistribution");
			for (IElement n_ : this.g.getNodes()) {
				UndirectedNode n = (UndirectedNode) n_;
				this.degree.incr(n.getDegree());
			}
			return true;
		} else if (this.g.getGraphDatastructures().isNodeType(
				DirectedNode.class)) {
			this.degree = new DistributionInt("DegreeDistribution");
			this.inDegree = new DistributionInt("InDegreeDistribution");
			this.outDegree = new DistributionInt("OutDegreeDistribution");
			for (IElement n_ : this.g.getNodes()) {
				DirectedNode n = (DirectedNode) n_;
				this.degree.incr(n.getDegree());
				this.inDegree.incr(n.getInDegree());
				this.outDegree.incr(n.getOutDegree());
			}
			return true;
		}
		return false;
	}

}
