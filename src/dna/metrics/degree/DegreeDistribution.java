package dna.metrics.degree;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;

public abstract class DegreeDistribution extends Metric {

	protected BinnedIntDistr degree;
	protected BinnedIntDistr inDegree;
	protected BinnedIntDistr outDegree;

	public DegreeDistribution(String name, Parameter... p) {
		super(name, MetricType.exact, p);
	}

	public DegreeDistribution(String name, String[] nodeTypes, Parameter... p) {
		super(name, MetricType.exact, nodeTypes, p);
	}

	@Override
	public Value[] getValues() {
		if (this.g.isDirected()) {
			Value minIn = new Value("InDegreeMin",
					this.inDegree.getMinNonZeroIndex());
			Value maxIn = new Value("InDegreeMax",
					this.inDegree.getMaxNonZeroIndex());
			Value minOut = new Value("OutDegreeMin",
					this.outDegree.getMinNonZeroIndex());
			Value maxOut = new Value("OutDegreeMax",
					this.outDegree.getMaxNonZeroIndex());
			Value min = new Value("DegreeMin", this.degree.getMinNonZeroIndex());
			Value max = new Value("DegreeMax", this.degree.getMaxNonZeroIndex());
			return new Value[] { minIn, maxIn, minOut, maxOut, min, max };
		} else {
			Value min = new Value("DegreeMin", this.degree.getMinNonZeroIndex());
			Value max = new Value("DegreeMax", this.degree.getMaxNonZeroIndex());
			return new Value[] { min, max };
		}
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		if (this.g.isDirected()) {
			return new Distr<?, ?>[] { this.degree, this.inDegree,
					this.outDegree };
		} else {
			return new Distr<?, ?>[] { this.degree };
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
		equals &= this.degree.equalsVerbose(dd.degree);
		if (this.inDegree != null) {
			equals &= this.inDegree.equalsVerbose(dd.inDegree);
			equals &= this.outDegree.equalsVerbose(dd.outDegree);
		}
		return equals;
	}

	@Override
	public boolean isApplicable(IGraph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	protected boolean compute() {
		if (this.g.isDirected()) {
			this.degree = new BinnedIntDistr("DegreeDistribution");
			this.inDegree = new BinnedIntDistr("InDegreeDistribution");
			this.outDegree = new BinnedIntDistr("OutDegreeDistribution");
			for (IElement n_ : this.getNodesOfAssignedTypes()) {
				DirectedNode n = (DirectedNode) n_;
				this.degree.incr(n.getDegree());
				this.inDegree.incr(n.getInDegree());
				this.outDegree.incr(n.getOutDegree());
			}
		} else {
			this.degree = new BinnedIntDistr("DegreeDistribution");
			this.inDegree = null;
			this.outDegree = null;
			for (IElement n_ : this.getNodesOfAssignedTypes()) {
				UndirectedNode n = (UndirectedNode) n_;
				this.degree.incr(n.getDegree());
			}
		}
		return true;
	}

}
