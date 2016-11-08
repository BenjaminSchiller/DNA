package dna.metrics.degree;

import java.util.Iterator;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.multi.DoubleMultiWeight;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;

/**
 * A metric computing the weighted degree distribution of a certain index of a
 * given DoubleMultiWeight.
 * 
 * @author Rwilmes
 * 
 */
public abstract class WeightedDegreeDistributionMulti extends Metric {

	protected BinnedIntDistr weightedDegree;
	protected BinnedIntDistr weightedInDegree;
	protected BinnedIntDistr weightedOutDegree;
	protected BinnedIntDistr weightedDegreeBalancePos;
	protected BinnedIntDistr weightedDegreeBalanceNeg;

	protected int index;
	protected int binsize;

	public WeightedDegreeDistributionMulti(String name, Parameter... p) {
		super(name, MetricType.exact, p);
		this.binsize = 1;
	}

	public WeightedDegreeDistributionMulti(String name, int index,
			String[] nodeTypes, Parameter... p) {
		super(name, MetricType.exact, nodeTypes, p);
		this.index = index;
		this.binsize = 1;
	}

	public WeightedDegreeDistributionMulti(String name, int index, int binsize) {
		this(name);
		this.index = index;
		this.binsize = binsize;
	}

	public WeightedDegreeDistributionMulti(String name, int index,
			String[] nodeTypes, int binsize, Parameter... p) {
		super(name, MetricType.exact, nodeTypes, p);
		this.index = index;
		this.binsize = binsize;
	}

	@Override
	public Value[] getValues() {
		if (this.g.isDirected()) {
			Value minIn = new Value("WeightedInDegreeMin",
					this.weightedInDegree.getMinNonZeroIndex());
			Value maxIn = new Value("WeightedInDegreeMax",
					this.weightedInDegree.getMaxNonZeroIndex());
			Value avgIn = new Value("WeightedInDegreeAvg",
					this.weightedInDegree.computeAverage());
			Value minOut = new Value("WeightedOutDegreeMin",
					this.weightedOutDegree.getMinNonZeroIndex());
			Value maxOut = new Value("WeightedOutDegreeMax",
					this.weightedOutDegree.getMaxNonZeroIndex());
			Value avgOut = new Value("WeightedOutDegreeAvg",
					this.weightedOutDegree.computeAverage());
			Value min = new Value("WeightedDegreeMin",
					this.weightedDegree.getMinNonZeroIndex());
			Value max = new Value("WeightedDegreeMax",
					this.weightedDegree.getMaxNonZeroIndex());
			Value avg = new Value("WeightedDegreeAvg",
					this.weightedDegree.computeAverage());
			Value minPosBal = new Value("WeightedDegreeBalancePosMin",
					this.weightedDegreeBalancePos.getMinNonZeroIndex());
			Value maxPosBal = new Value("WeightedDegreeBalancePosMax",
					this.weightedDegreeBalancePos.getMaxNonZeroIndex());
			Value avgPosBal = new Value("WeightedDegreeBalancePosAvg",
					this.weightedDegreeBalancePos.computeAverage());
			Value minNegBal = new Value("WeightedDegreeBalanceNegMin",
					this.weightedDegreeBalancePos.getMinNonZeroIndex());
			Value maxNegBal = new Value("WeightedDegreeBalanceNegMax",
					this.weightedDegreeBalancePos.getMaxNonZeroIndex());
			Value avgNegBal = new Value("WeightedDegreeBalanceNegAvg",
					this.weightedDegreeBalancePos.computeAverage());
			return new Value[] { minIn, maxIn, avgIn, minOut, maxOut, avgOut,
					min, max, avg, minPosBal, maxPosBal, avgPosBal, minNegBal,
					maxNegBal, avgNegBal };
		} else {
			Value min = new Value("WeightedDegreeMin",
					this.weightedDegree.getMinNonZeroIndex());
			Value max = new Value("WeightedDegreeMax",
					this.weightedDegree.getMaxNonZeroIndex());
			Value avg = new Value("WeightedDegreeAvg",
					this.weightedDegree.computeAverage());
			return new Value[] { min, max, avg };
		}
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		if (this.g.isDirected()) {
			return new Distr<?, ?>[] { this.weightedDegree,
					this.weightedInDegree, this.weightedOutDegree };
		} else {
			return new Distr<?, ?>[] { this.weightedDegree };
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
		return m instanceof WeightedDegreeDistribution;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof WeightedDegreeDistribution)) {
			return false;
		}
		WeightedDegreeDistribution wdd = (WeightedDegreeDistribution) m;
		boolean equals = true;
		equals &= this.weightedDegree.equalsVerbose(wdd.weightedDegree);
		if (this.weightedInDegree != null) {
			equals &= this.weightedInDegree.equalsVerbose(wdd.weightedInDegree);
			equals &= this.weightedOutDegree
					.equalsVerbose(wdd.weightedOutDegree);
		}
		return equals;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& (g.getGraphDatastructures()
						.isEdgeWeightType(DoubleMultiWeight.class));
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& (b.getGraphDatastructures()
						.isEdgeWeightType(DoubleMultiWeight.class));
	}

	protected boolean compute() {
		if (this.g.isDirected()) {
			this.weightedDegree = new BinnedIntDistr(
					"WeightedDegreeDistribution", binsize);
			this.weightedInDegree = new BinnedIntDistr(
					"WeightedInDegreeDistribution", binsize);
			this.weightedOutDegree = new BinnedIntDistr(
					"WeightedOutDegreeDistribution", binsize);
			this.weightedDegreeBalancePos = new BinnedIntDistr(
					"WeightedDegreePosBalanceDistribution", binsize);
			this.weightedDegreeBalanceNeg = new BinnedIntDistr(
					"WeightedDegreeNegBalanceDistribution", binsize);
			for (IElement n_ : this.getNodesOfAssignedTypes()) {
				DirectedNode n = (DirectedNode) n_;
				int inWeight = 0;
				int outWeight = 0;

				// out-going
				Iterator<IElement> iterator = n.getOutgoingEdges().iterator();
				while (iterator.hasNext()) {
					DoubleMultiWeight w = (DoubleMultiWeight) ((IWeightedEdge) iterator
							.next()).getWeight();
					outWeight += (int) Math.floor(w.getWeight(this.index));
				}

				// incoming
				iterator = n.getIncomingEdges().iterator();
				while (iterator.hasNext()) {
					DoubleMultiWeight w = (DoubleMultiWeight) ((IWeightedEdge) iterator
							.next()).getWeight();
					inWeight += (int) Math.floor(w.getWeight(this.index));
				}

				this.weightedDegree.incr((outWeight + inWeight));
				this.weightedInDegree.incr(inWeight);
				this.weightedOutDegree.incr(outWeight);

				if (inWeight <= outWeight) {
					this.weightedDegreeBalancePos.incr(outWeight - inWeight);
				} else {
					this.weightedDegreeBalanceNeg.incr(Math.abs(outWeight
							- inWeight));
				}
			}
		} else {
			this.weightedDegree = new BinnedIntDistr(
					"WeightedDegreeDistribution", binsize);
			this.weightedInDegree = null;
			this.weightedOutDegree = null;
			for (IElement n_ : this.getNodesOfAssignedTypes()) {
				UndirectedNode n = (UndirectedNode) n_;
				int weight = 0;
				Iterator<IElement> iterator = n.getEdges().iterator();
				while (iterator.hasNext()) {
					DoubleMultiWeight w = (DoubleMultiWeight) ((IWeightedEdge) iterator
							.next()).getWeight();
					weight += (int) Math.floor(w.getWeight(this.index));
				}

				this.weightedDegree.incr(n.getDegree(), weight);
			}
		}
		return true;
	}
}
