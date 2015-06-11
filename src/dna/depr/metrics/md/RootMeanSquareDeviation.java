package dna.depr.metrics.md;

import dna.depr.metrics.MetricOld;
import dna.graph.Graph;
import dna.graph.weights.Double2dWeight;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Int2dWeight;
import dna.graph.weights.Int3dWeight;
import dna.graph.weights.IntWeight;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distributions.BinnedDistributionInt;
import dna.series.data.distributions.Distribution;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

/**
 * 
 * Root Mean Square Deviation of the position changes of nodes in a
 * 3-dimensional space. For each node, the difference between their position
 * from one batch to the other is taken as distance / movement of the node. For
 * the first snapshot, all nodes are initialize dwith their current position.
 * Since there is no point of reference to compute the distance to, their
 * deviation in this first step is 0.
 * http://en.wikipedia.org/wiki/Root_mean_square_deviation
 * 
 * @author benni
 * 
 */
public abstract class RootMeanSquareDeviation extends MetricOld {

	protected int changes;

	protected double rmsd;

	protected BinnedDistributionInt distr;

	public RootMeanSquareDeviation(String name, ApplicationType type,
			Metric.MetricType metricType) {
		super(name, type, metricType);
	}

	protected void initDistr() {
		this.distr = new BinnedDistributionInt("DistanceDistribution", 0.05);
	}

	@Override
	public void init_() {
		this.changes = 0;
		this.rmsd = 0;
		this.distr = null;
	}

	@Override
	public void reset_() {
		this.changes = 0;
		this.rmsd = 0;
		this.distr = null;
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("RootMeanSquareDeviation", this.rmsd);
		Value v2 = new Value("Changes", this.changes);
		return new Value[] { v1, v2 };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] { this.distr };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m == null || !(m instanceof RootMeanSquareDeviation)) {
			return false;
		}
		RootMeanSquareDeviation m2 = (RootMeanSquareDeviation) m;
		boolean success = true;
		success &= DataUtils.equals(this.rmsd, m2.rmsd,
				"RootMeanSquareDeviation");
		success &= ArrayUtils.equals(this.distr.getValues(),
				m2.distr.getValues(), "DistanceDistribution");
		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& g.getGraphDatastructures().isNodeWeightType(
						DoubleWeight.class, Double2dWeight.class,
						Double3dWeight.class, IntWeight.class,
						Int2dWeight.class, Int3dWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& b.getGraphDatastructures().isNodeWeightType(
						DoubleWeight.class, Double2dWeight.class,
						Double3dWeight.class, IntWeight.class,
						Int2dWeight.class, Int3dWeight.class);
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof RootMeanSquareDeviation;
	}

}
