package dna.metrics.betweenessCentrality;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class BetweenessCentrality extends Metric {

	// protected HashMap<Node, Double> bC;

	protected NodeValueList bCC;
	protected double bCSum;

	protected HashMap<Node, HashMap<Node, HashSet<Node>>> parents;
	protected HashMap<Node, HashMap<Node, Integer>> distances;
	protected HashMap<Node, HashMap<Node, Integer>> spcs;
	protected HashMap<Node, HashMap<Node, Double>> accSums;

	public BetweenessCentrality(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		// this.bC = new HashMap<Node, Double>();
		this.parents = new HashMap<>();
		this.distances = new HashMap<>();
		this.spcs = new HashMap<>();
		this.accSums = new HashMap<>();
		this.bCC = new NodeValueList("BC_Score",
				new double[this.g.getMaxNodeIndex() + 1]);
		this.bCSum = 0d;
	}

	@Override
	public void reset_() {
		this.parents = new HashMap<>();
		this.distances = new HashMap<>();
		this.spcs = new HashMap<>();
		this.accSums = new HashMap<>();
		// this.bC = new HashMap<Node, Double>();
		this.bCC = new NodeValueList("BC_Score",
				new double[this.g.getMaxNodeIndex() + 1]);
		this.bCSum = 0d;
	}

	@Override
	public boolean compute() {
		Queue<Node> q = new LinkedList<Node>();
		Stack<Node> s = new Stack<Node>();

		for (IElement ie : g.getNodes()) {
			Node n = (Node) ie;
			// stage ONE
			s.clear();
			q.clear();
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

			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				// stage 2
				while (!q.isEmpty()) {
					DirectedNode v = (DirectedNode) q.poll();
					s.push(v);
					for (IElement iEdges : v.getOutgoingEdges()) {
						DirectedEdge edge = (DirectedEdge) iEdges;
						DirectedNode w = edge.getDifferingNode(v);

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
						UndirectedNode w = edge.getDifferingNode(v);

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
					// this.bC.get(w);
					// this.bC.put(w, currentScore + sums.get(w));
					this.bCC.setValue(w.getIndex(), currentScore + sums.get(w));
					this.bCSum += sums.get(w);
				}
			}
			// parents.put(n, p);
			// distances.put(n, d);
			// spcs.put(n, spc);
			// accSums.put(n, sums);
		}

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof BetweenessCentrality)) {
			return false;
		}
		boolean success = true;
		BetweenessCentrality bc = (BetweenessCentrality) m;

		for (IElement ie1 : g.getNodes()) {
			Node n1 = (Node) ie1;
			for (IElement ie2 : g.getNodes()) {
				Node n2 = (Node) ie2;

				if (!this.spcs.get(n1).get(n2).equals(bc.spcs.get(n1).get(n2))) {
					System.out.println("diff at Tree " + n1 + "in Node n " + n2
							+ " expected SPC "
							+ this.spcs.get(n1).get(n2).intValue() + " is "
							+ bc.spcs.get(n1).get(n2).intValue());
					success = false;
				}

				if (!this.parents.get(n1).get(n2)
						.containsAll(bc.parents.get(n1).get(n2))
						|| this.parents.get(n1).get(n2).size() != bc.parents
								.get(n1).get(n2).size()) {
					System.out.println("diff at Tree " + n1 + "in Node n " + n2
							+ " expected parents "
							+ this.parents.get(n1).get(n2) + " is "
							+ bc.parents.get(n1).get(n2));
					success = false;
				}

				if (Math.abs(this.accSums.get(n1).get(n2).doubleValue()
						- bc.accSums.get(n1).get(n2).doubleValue()) > 0.000001) {
					System.out.println("diff at Tree " + n1 + "in Node n " + n2
							+ " expected Sum " + this.accSums.get(n1).get(n2)
							+ " is " + bc.accSums.get(n1).get(n2)
							+ " height == " + bc.distances.get(n1).get(n2));
					success = false;
				}

				if (!this.distances.get(n1).get(n2)
						.equals(bc.distances.get(n1).get(n2))) {
					System.out.println("diff at Tree " + n1 + "in Node n " + n2
							+ " expected dist "
							+ this.distances.get(n1).get(n2) + " is "
							+ bc.distances.get(n1).get(n2));
					success = false;
				}

			}
		}

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

		return success;
	}

	@Override
	public Value[] getValues() {
		// Value v1 = new Value("median", getMedian());
		Value v2 = new Value("avg_bc", bCSum / (double) g.getNodeCount());
		return new Value[] { v2 };
	}

	private double getMedian() {
		double[] sortedArray = bCC.getValues();
		Arrays.sort(sortedArray);
		double median;
		if (sortedArray.length % 2 == 0) {
			median = ((double) sortedArray[sortedArray.length / 2] + (double) sortedArray[sortedArray.length / 2 + 1]) / 2;
		} else {
			median = (double) sortedArray[sortedArray.length / 2];
		}
		return median;

	}

	@Override
	public Distribution[] getDistributions() {
		// Distribution d1 = new Distribution("BetweenessCentrality",
		// getDistribution(this.bC));
		return new Distribution[] {};

	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		this.bCC.toString();
		return new NodeValueList[] { this.bCC };
	}

	private double[] getDistribution(
			HashMap<Node, Double> betweeneesCentralityScore2) {
		double[] temp = new double[betweeneesCentralityScore2.size()];
		int counter = 0;
		for (Node i : betweeneesCentralityScore2.keySet()) {
			temp[counter] = betweeneesCentralityScore2.get(i);
			counter++;
		}
		return temp;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof BetweenessCentrality;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| DirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| DirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

}
