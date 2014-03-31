package dna.metrics.md;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.DirectedDoubleArrayWeightedNode;
import dna.graph.nodes.DirectedIntArrayWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedDoubleArrayWeightedNode;
import dna.graph.nodes.UndirectedIntArrayWeightedNode;
import dna.metrics.Metric;
import dna.series.data.BinnedDistributionInt;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
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
 * http://en.wikipedia.org/wiki/Root-mean-square_deviation_of_atomic_positions
 * 
 * @author benni
 * 
 */
public abstract class RootMeanSquareDeviation<W> extends Metric {

	protected int changes;

	protected double rmsd;

	protected BinnedDistributionInt distr;

	protected HashMap<Integer, W> positions;

	public RootMeanSquareDeviation(String name, ApplicationType type,
			MetricType metricType) {
		super(name, type, metricType);
	}

	protected void initDistr() {
		this.distr = new BinnedDistributionInt("DeviationDistribution", 0.1,
				new int[0], 0);
	}

	/**
	 * Returns the current weight of the given node. null is returned in case
	 * the node does not have a double- or int-array weight.
	 * 
	 * @param n
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected W getWeight(Node n) {
		if (n instanceof DirectedIntArrayWeightedNode) {
			return (W) ((DirectedIntArrayWeightedNode) n).getWeight();
		} else if (n instanceof DirectedDoubleArrayWeightedNode) {
			return (W) ((DirectedDoubleArrayWeightedNode) n).getWeight();
		} else if (n instanceof UndirectedIntArrayWeightedNode) {
			return (W) ((UndirectedIntArrayWeightedNode) n).getWeight();
		} else if (n instanceof UndirectedDoubleArrayWeightedNode) {
			return (W) ((UndirectedDoubleArrayWeightedNode) n).getWeight();
		}
		return null;
	}

	/**
	 * 
	 * Computes and returns the deviation between the two given positions, i.e.
	 * the squared distance between the two positions.
	 * 
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	protected double getDeviation(W pos1, W pos2) {
		if (pos1 instanceof int[]) {
			int[] before = (int[]) pos1;
			int[] after = (int[]) pos2;
			if (before.length < after.length) {
				before = new int[after.length];
			}
			double deviation = 0;
			for (int i = 0; i < before.length; i++) {
				int diff = before[i] - after[i];
				deviation += (double) (diff * diff);
			}
			return deviation;
		} else if (pos1 instanceof double[]) {
			double[] before = (double[]) pos1;
			double[] after = (double[]) pos2;
			if (before.length < after.length) {
				before = new double[after.length];
			}
			double deviation = 0;
			for (int i = 0; i < before.length; i++) {
				double diff = before[i] - after[i];
				deviation += diff * diff;
			}
			return deviation;
		}
		return -1;
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

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof RootMeanSquareDeviation)) {
			return false;
		}
		RootMeanSquareDeviation m2 = (RootMeanSquareDeviation) m;
		boolean success = true;
		success &= DataUtils.equals(this.rmsd, m2.rmsd,
				"RootMeanSquareDeviation");
		success &= ArrayUtils.equals(this.distr.getIntValues(),
				m2.distr.getIntValues(), "DeviationDistribution");
		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().getNodeType()
				.isAssignableFrom(DirectedIntArrayWeightedNode.class)
				|| g.getGraphDatastructures()
						.getNodeType()
						.isAssignableFrom(DirectedDoubleArrayWeightedNode.class)
				|| g.getGraphDatastructures().getNodeType()
						.isAssignableFrom(UndirectedIntArrayWeightedNode.class)
				|| g.getGraphDatastructures()
						.getNodeType()
						.isAssignableFrom(
								UndirectedDoubleArrayWeightedNode.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().getNodeType()
				.isAssignableFrom(DirectedIntArrayWeightedNode.class)
				|| b.getGraphDatastructures()
						.getNodeType()
						.isAssignableFrom(DirectedDoubleArrayWeightedNode.class)
				|| b.getGraphDatastructures().getNodeType()
						.isAssignableFrom(UndirectedIntArrayWeightedNode.class)
				|| b.getGraphDatastructures()
						.getNodeType()
						.isAssignableFrom(
								UndirectedDoubleArrayWeightedNode.class);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RootMeanSquareDeviation;
	}

}
