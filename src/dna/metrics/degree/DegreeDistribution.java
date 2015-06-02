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
	
	public DegreeDistribution(String name, MetricType type, Parameter... p) {
		super(name, type, p);
	}

	@Override
	public Value[] getValues() {
		if (this.g.isDirected()) {
			Value minIn = new Value("inDegreeMin", this.inDegree.getMin());
			Value maxIn = new Value("inDegreeMax", this.inDegree.getMax());
			Value minOut = new Value("outDegreeMin", this.outDegree.getMin());
			Value maxOut = new Value("outDegreeMax", this.outDegree.getMax());
			Value min = new Value("degreeMin", this.degree.getMin());
			Value max = new Value("degreeMax", this.degree.getMax());
			return new Value[] { minIn, maxIn, minOut, maxOut, min, max };
		} else {
			Value min = new Value("degreeMin", this.degree.getMin());
			Value max = new Value("degreeMax", this.degree.getMax());
			return new Value[] { min, max };
		}
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
