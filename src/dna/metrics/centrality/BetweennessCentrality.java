package dna.metrics.centrality;

import java.util.HashMap;
import java.util.Map.Entry;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

public abstract class BetweennessCentrality extends Metric implements IMetric {

	// protected HashMap<Node, Double> bC;

	public NodeValueList bCC;
	public double bCSum;

	public BinnedDoubleDistr binnedBC;
	public int sumShortestPaths;

	public BetweennessCentrality(String name) {
		super(name, MetricType.exact);
	}

	public BetweennessCentrality(String name, String[] nodeTypes) {
		super(name, MetricType.exact, nodeTypes);
	}

	// private double getMedian() {
	// double[] sortedArray = bCC.getValues();
	// Arrays.sort(sortedArray);
	// double median;
	// if (sortedArray.length % 2 == 0) {
	// median = ((double) sortedArray[sortedArray.length / 2] + (double)
	// sortedArray[sortedArray.length / 2 + 1]) / 2;
	// } else {
	// median = (double) sortedArray[sortedArray.length / 2];
	// }
	// return median;
	// }

	@Override
	public Value[] getValues() {
		// Value v1 = new Value("median", getMedian());
		// Value v2 = new Value("avg_bc", bCSum / (double) g.getNodeCount());
		Value v3 = new Value("bCSum", bCSum);
		Value v4 = new Value("sumShortestPaths", sumShortestPaths);
		return new Value[] { v3, v4 };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		computeBinnedBC();
		return new Distr<?, ?>[] { binnedBC };

	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		this.bCC.toString();
		return new NodeValueList[] { this.bCC };
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof BetweennessCentrality;
	}

	@Override
	public boolean equals(IMetric m) {
		if (!(m instanceof BetweennessCentrality)) {
			return false;
		}
		boolean success = true;
		BetweennessCentrality bc = (BetweennessCentrality) m;
		success &= ArrayUtils
				.equals(bCC.getValues(), bc.bCC.getValues(), "bCC");
		success &= DataUtils.equals(sumShortestPaths, bc.sumShortestPaths,
				"sumShortestPaths");
		if (Math.abs(bc.bCSum - bCSum) > 0.00001) {
			success &= DataUtils.equals(bCSum, bc.bCSum, "bCSum");
		}
		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	protected void computeBinnedBC() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (Double d : bCC.getValues()) {
			max = (d > max) ? d : max;
			min = (d < min) ? d : min;
		}

		for (Double d : bCC.getValues()) {
			double norm = 0;
			if (sumShortestPaths != 0)
				norm = d / sumShortestPaths;
			else
				norm = 0.0;

			binnedBC.incr(norm);
		}
	}

	protected int sumSPFromHM(HashMap<Node, Integer> spc, Node n) {
		int sum = 0;
		for (Entry<Node, Integer> e : spc.entrySet()) {
			if (!e.getKey().equals(n)) {
				sum += e.getValue();
			}
		}
		return sum;
	}

	public int getSumShortestPaths() {
		return sumShortestPaths;
	}

}
