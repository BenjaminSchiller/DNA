package dna.depr.metrics.md;

import java.util.HashMap;
import java.util.LinkedList;

import dna.depr.metrics.Metric;
import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.weights.Double2dWeight;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Int2dWeight;
import dna.graph.weights.Int3dWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;
import dna.graph.weights.distances.EuclideanDistance;
import dna.metricsNew.MetricNew;
import dna.series.data.BinnedDistributionInt;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.IntParameter;

/**
 * Root Mean Square Fluctuation of the position changes of nodes. The parameter
 * "steps" determines the time interval (number of past batches) over which the
 * fluctuation is computed. For each node, the fluctuation is computed as
 * follows: it is the sum of the distance between the position during the last
 * $steps batches and the average position over this time span. For the first
 * snapshot, all nodes are initialized with their current position. Since there
 * is no point of reference to compute the distance to, their fluctuation in
 * this first step is 0.
 * http://en.wikipedia.org/wiki/Root_mean_square_fluctuation
 * 
 * @author benni
 * 
 */
public abstract class RootMeanSquareFluctuation extends Metric {

	protected HashMap<Node, LinkedList<double[]>> positions;

	protected NodeValueList rmsf;

	protected BinnedDistributionInt rmsfD;

	protected int steps;

	public RootMeanSquareFluctuation(String name, ApplicationType type,
			MetricNew.MetricType metricType, int steps) {
		super(name, type, metricType, new IntParameter("STEPS", steps));
		this.steps = steps;
	}

	@Override
	public void init_() {
		this.rmsf = new NodeValueList("RootMeanSquareFluctuation", 0);
		this.rmsfD = new BinnedDistributionInt(
				"RootMeanSquareFluctuation--Distribution", 0.05);
	}

	@Override
	public void reset_() {
		this.positions = null;
		this.init_();
	}

	@Override
	public Value[] getValues() {
		return new Value[] { new Value("RootMeanSquareFluctuation--average",
				ArrayUtils.avg(this.rmsf.getValues())) };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] { this.rmsfD };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] { this.rmsf };
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof RootMeanSquareFluctuation)) {
			return false;
		}
		RootMeanSquareFluctuation rmsf = (RootMeanSquareFluctuation) m;
		if (this.steps != rmsf.steps) {
			return false;
		}
		boolean success = true;
		success &= ArrayUtils.equals(this.rmsf.getValues(),
				rmsf.rmsf.getValues(), "RootMeanSquareFluctuation");
		success &= ArrayUtils.equals(this.rmsfD.getIntValues(),
				rmsf.rmsfD.getIntValues(),
				"RootMeanSquareFluctuation--Distribution");
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
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RootMeanSquareFluctuation
				&& ((RootMeanSquareFluctuation) m).steps == this.steps;
	}

	protected LinkedList<double[]> update(Node n, Weight w) {
		LinkedList<double[]> positions = this.positions.get(n);
		if (positions == null) {
			positions = new LinkedList<double[]>();
			this.positions.put(n, positions);
		}
		positions.add(this.getWeight(w));
		if (positions.size() > this.steps) {
			positions.removeFirst();
		}
		return positions;
	}

	protected double[] getWeight(Weight w) {
		if (w instanceof DoubleWeight) {
			return new double[] { ((DoubleWeight) w).getWeight() };
		}
		if (w instanceof Double2dWeight) {
			return new double[] { ((Double2dWeight) w).getX(),
					((Double2dWeight) w).getY() };
		}
		if (w instanceof Double3dWeight) {
			return new double[] { ((Double3dWeight) w).getX(),
					((Double3dWeight) w).getY(), ((Double3dWeight) w).getZ() };
		}
		if (w instanceof IntWeight) {
			return new double[] { ((IntWeight) w).getWeight() };
		}
		if (w instanceof Int2dWeight) {
			return new double[] { ((Int2dWeight) w).getX(),
					((Int2dWeight) w).getY() };
		}
		if (w instanceof Int3dWeight) {
			return new double[] { ((Int3dWeight) w).getX(),
					((Int3dWeight) w).getY(), ((Int3dWeight) w).getZ() };
		}
		return null;
	}

	protected int getDimensions() {
		if (this.g.getGraphDatastructures().isNodeWeightType(IntWeight.class,
				DoubleWeight.class)) {
			return 1;
		}
		if (this.g.getGraphDatastructures().isNodeWeightType(Int2dWeight.class,
				Double2dWeight.class)) {
			return 2;
		}
		if (this.g.getGraphDatastructures().isNodeWeightType(Int3dWeight.class,
				Double3dWeight.class)) {
			return 3;
		}
		return -1;
	}

	protected double[] computeMeanPosition(LinkedList<double[]> positions) {
		double[] mean = new double[this.getDimensions()];
		for (double[] pos : positions) {
			for (int i = 0; i < mean.length; i++) {
				mean[i] += pos[i];
			}
		}
		for (int i = 0; i < mean.length; i++) {
			mean[i] /= positions.size();
		}
		return mean;
	}

	protected double computeRMSF(LinkedList<double[]> positions) {
		double[] mean = this.computeMeanPosition(positions);
		double msf = 0;
		for (double[] pos : positions) {
			msf += EuclideanDistance.distSquared(pos, mean);
		}
		msf /= positions.size();
		msf = Math.sqrt(msf);
		return msf;
	}

}
