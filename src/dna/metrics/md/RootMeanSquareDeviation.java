package dna.metrics.md;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.weights.Double2dWeight;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Int2dWeight;
import dna.graph.weights.Int3dWeight;
import dna.graph.weights.Weight;
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
public abstract class RootMeanSquareDeviation extends Metric {

	protected int changes;

	protected double rmsd;

	protected BinnedDistributionInt distr;

	protected HashMap<Integer, Weight> positions;

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
	protected Weight getWeight(Node n) {
		if (n instanceof IWeightedNode) {
			return ((IWeightedNode) n).getWeight();
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
	protected double getDeviation(Weight pos1, Weight pos2) {
		if (pos1 instanceof Int2dWeight || pos1 instanceof Int3dWeight) {
			int[] before, after;
			if (pos1 instanceof Int2dWeight) {
				before = new int[] { ((Int2dWeight) pos1).getX(),
						((Int2dWeight) pos2).getY() };
				after = new int[] { ((Int2dWeight) pos2).getX(),
						((Int2dWeight) pos2).getY() };
			} else {
				before = new int[] { ((Int3dWeight) pos1).getX(),
						((Int3dWeight) pos2).getY(),
						((Int3dWeight) pos2).getZ() };
				after = new int[] { ((Int3dWeight) pos2).getX(),
						((Int3dWeight) pos2).getY(),
						((Int3dWeight) pos2).getZ() };
			}

			if (before.length < after.length) {
				before = new int[after.length];
			}
			double deviation = 0;
			for (int i = 0; i < before.length; i++) {
				int diff = before[i] - after[i];
				deviation += (double) (diff * diff);
			}
			return deviation;
		} else if (pos1 instanceof Double2dWeight
				|| pos1 instanceof Double3dWeight) {
			double[] before, after;
			if (pos1 instanceof Double2dWeight) {
				before = new double[] { ((Double2dWeight) pos1).getX(),
						((Double2dWeight) pos2).getY() };
				after = new double[] { ((Double2dWeight) pos2).getX(),
						((Double2dWeight) pos2).getY() };
			} else {
				before = new double[] { ((Double3dWeight) pos1).getX(),
						((Double3dWeight) pos2).getY(),
						((Double3dWeight) pos2).getZ() };
				after = new double[] { ((Double3dWeight) pos2).getX(),
						((Double3dWeight) pos2).getY(),
						((Double3dWeight) pos2).getZ() };
			}

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
		Class<? extends Node> nodeType = g.getGraphDatastructures()
				.getNodeType();
		if (!IWeightedNode.class.isAssignableFrom(nodeType))
			return false;

		Class<? extends Weight> nodeWeightType = g.getGraphDatastructures()
				.getNodeWeightType();
		if (!Double2dWeight.class.isAssignableFrom(nodeWeightType)
				&& !Double3dWeight.class.isAssignableFrom(nodeWeightType)
				&& !Int2dWeight.class.isAssignableFrom(nodeWeightType)
				&& !Int3dWeight.class.isAssignableFrom(nodeWeightType))
			return false;

		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		Class<? extends Node> nodeType = b.getGraphDatastructures()
				.getNodeType();
		if (!IWeightedNode.class.isAssignableFrom(nodeType))
			return false;

		Class<? extends Weight> nodeWeightType = b.getGraphDatastructures()
				.getNodeWeightType();
		if (!Double2dWeight.class.isAssignableFrom(nodeWeightType)
				&& !Double3dWeight.class.isAssignableFrom(nodeWeightType)
				&& !Int2dWeight.class.isAssignableFrom(nodeWeightType)
				&& !Int3dWeight.class.isAssignableFrom(nodeWeightType))
			return false;

		return true;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RootMeanSquareDeviation;
	}

}
