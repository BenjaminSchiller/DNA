package dna.metrics.centrality;

import java.util.HashMap;
import java.util.Map.Entry;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.BinnedDistributionDouble;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class BetweennessCentrality extends Metric implements IMetric {

	// protected HashMap<Node, Double> bC;

	protected NodeValueList bCC;
	protected double bCSum;

	protected BinnedDistributionDouble binnedBC;
	protected int sumShortestPaths;

	public BetweennessCentrality(String name) {
		super(name);
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
		Value v2 = new Value("avg_bc", bCSum / (double) g.getNodeCount());
		return new Value[] { v2 };
	}

	@Override
	public Distribution[] getDistributions() {
		computeBinnedBC();
		return new Distribution[] { binnedBC };

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

		/*
		 * detailed comparison is no longer possible -> only saved in update
		 * variant
		 */

		for (IElement ie : g.getNodes()) {
			Node n = (Node) ie;
			if (Math.abs(this.bCC.getValue(n.getIndex())
					- bc.bCC.getValue(n.getIndex())) > 0.0001) {
				System.out.println("diff at Node n " + n + " expected Score "
						+ this.bCC.getValue(n.getIndex()) + " is "
						+ bc.bCC.getValue(n.getIndex()));
				success = false;
			}

		}

		if (sumShortestPaths != bc.getSumShortestPaths()) {
			success = false;
			System.out.println("diff at sum of shortest paths: "
					+ sumShortestPaths + " is expected. Result is: "
					+ bc.getSumShortestPaths());
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
