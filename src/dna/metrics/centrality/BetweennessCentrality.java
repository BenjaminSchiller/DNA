package dna.metrics.centrality;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
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

	public BetweennessCentrality(String name, MetricType metricType) {
		super(name, metricType);
	}

	public BetweennessCentrality(String name, MetricType metricType,
			String[] nodeTypes) {
		super(name, metricType, nodeTypes);
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

	protected void initProperties() {
		this.bCC = new NodeValueList("BC_Score",
				new double[this.g.getMaxNodeIndex() + 1]);
		this.binnedBC = new BinnedDoubleDistr("Normalized-BC", 0.01d);
		this.bCSum = 0d;
		this.sumShortestPaths = 0;
	}

	protected void process(Node n) {
		// stage ONE

		Queue<Node> q = new LinkedList<Node>();
		Stack<Node> s = new Stack<Node>();
		HashMap<Node, HashSet<Node>> p = new HashMap<Node, HashSet<Node>>();
		HashMap<Node, Integer> d = new HashMap<Node, Integer>();
		HashMap<Node, Integer> spc = new HashMap<Node, Integer>();
		HashMap<Node, Double> sums = new HashMap<Node, Double>();

		for (IElement ieE : g.getNodes()) {
			Node t = (Node) ieE;
			if (t == n) {
				d.put(t, 0);
				spc.put(t, 1);
			} else {
				spc.put(t, 0);
				d.put(t, Integer.MAX_VALUE);
			}
			sums.put(t, 0d);
			p.put(t, new HashSet<Node>());
		}

		q.add(n);

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			// stage 2
			while (!q.isEmpty()) {
				DirectedNode v = (DirectedNode) q.poll();
				s.push(v);
				for (IElement iEdges : v.getOutgoingEdges()) {
					DirectedEdge edge = (DirectedEdge) iEdges;
					DirectedNode w = (DirectedNode) edge.getDifferingNode(v);

					if (d.get(w).equals(Integer.MAX_VALUE)) {
						q.add(w);
						d.put(w, d.get(v) + 1);
					}
					if (d.get(w).equals(d.get(v) + 1)) {
						spc.put(w, spc.get(w) + spc.get(v));
						p.get(w).add(v);
					}
				}
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			// stage 2
			while (!q.isEmpty()) {
				UndirectedNode v = (UndirectedNode) q.poll();
				s.push(v);

				for (IElement iEdges : v.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) iEdges;
					UndirectedNode w = (UndirectedNode) edge
							.getDifferingNode(v);

					if (d.get(w).equals(Integer.MAX_VALUE)) {
						q.add(w);
						d.put(w, d.get(v) + 1);
					}
					if (d.get(w).equals(d.get(v) + 1)) {
						spc.put(w, spc.get(w) + spc.get(v));
						p.get(w).add(v);
					}
				}
			}
		}

		// stage 3
		while (!s.isEmpty()) {
			Node w = s.pop();
			for (Node parent : p.get(w)) {
				double sumForCurretConnection = spc.get(parent)
						* (1 + sums.get(w)) / spc.get(w);
				sums.put(parent, sums.get(parent) + sumForCurretConnection);
			}
			if (w != n) {
				double currentScore = this.bCC.getValue(w.getIndex());
				this.bCC.setValue(w.getIndex(), currentScore + sums.get(w));
				this.bCSum += sums.get(w) - 3;
			}
		}

		sumShortestPaths += sumSPFromHM(spc, n);
	}

}
