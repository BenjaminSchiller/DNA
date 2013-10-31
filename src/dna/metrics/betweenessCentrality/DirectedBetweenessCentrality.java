package dna.metrics.betweenessCentrality;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class DirectedBetweenessCentrality extends Metric {

	protected HashMap<DirectedNode, Double> bC;
	protected HashMap<DirectedNode, HashMap<DirectedNode, HashSet<DirectedNode>>> parents;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> distances;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> spcs;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Double>> accSums;

	public DirectedBetweenessCentrality(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		this.bC = new HashMap<DirectedNode, Double>();
		this.parents = new HashMap<>();
		this.distances = new HashMap<>();
		this.spcs = new HashMap<>();
		this.accSums = new HashMap<>();
	}

	@Override
	public void reset_() {
		this.bC = new HashMap<DirectedNode, Double>();
		this.parents = new HashMap<>();
		this.distances = new HashMap<>();
		this.spcs = new HashMap<>();
		this.accSums = new HashMap<>();
	}

	@Override
	public boolean compute() {
		Queue<DirectedNode> q = new LinkedList<DirectedNode>();
		Stack<DirectedNode> s = new Stack<DirectedNode>();

		for (IElement ie : g.getNodes()) {
			DirectedNode t = (DirectedNode) ie;
			bC.put(t, 0d);
		}

		for (IElement ie : g.getNodes()) {
			DirectedNode n = (DirectedNode) ie;
			// stage ONE
			s.clear();
			q.clear();
			HashMap<DirectedNode, HashSet<DirectedNode>> p = new HashMap<DirectedNode, HashSet<DirectedNode>>();
			HashMap<DirectedNode, Integer> d = new HashMap<DirectedNode, Integer>();
			HashMap<DirectedNode, Integer> spc = new HashMap<DirectedNode, Integer>();
			HashMap<DirectedNode, Double> sums = new HashMap<DirectedNode, Double>();
			for (IElement ieE : g.getNodes()) {
				DirectedNode t = (DirectedNode) ieE;
				if (t == n) {
					d.put(t, 0);
					spc.put(t, 1);
				} else {
					spc.put(t, 0);
					d.put(t, Integer.MAX_VALUE);
				}
				sums.put(t, 0d);
				p.put(t, new HashSet<DirectedNode>());
			}

			q.add(n);

			// stage 2
			while (!q.isEmpty()) {
				DirectedNode v = q.poll();
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

			// stage 3
			while (!s.isEmpty()) {
				DirectedNode w = s.pop();
				for (DirectedNode parent : p.get(w)) {
					double sumForCurretConnection = spc.get(parent)
							* (1 + sums.get(w)) / spc.get(w);
					sums.put(parent, sums.get(parent) + sumForCurretConnection);
				}
				if (w != n) {
					double currentScore = this.bC.get(w);
					this.bC.put(w, currentScore + sums.get(w));
				}
			}
			parents.put(n, p);
			distances.put(n, d);
			spcs.put(n, spc);
			accSums.put(n, sums);
		}

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof DirectedBetweenessCentrality)) {
			return false;
		}
		boolean success = true;
		DirectedBetweenessCentrality bc = (DirectedBetweenessCentrality) m;

		for (IElement ie1 : g.getNodes()) {
			DirectedNode n1 = (DirectedNode) ie1;
			for (IElement ie2 : g.getNodes()) {
				DirectedNode n2 = (DirectedNode) ie2;

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
			DirectedNode n = (DirectedNode) ie;
			if (Math.abs(this.bC.get(n).doubleValue()
					- bc.bC.get(n).doubleValue()) > 0.0001) {
				System.out.println("diff at Node n " + n + " expected Score "
						+ this.bC.get(n) + " is " + bc.bC.get(n));
				success = false;
			}

		}

		return success;
	}

	@Override
	public Value[] getValues() {
		return new Value[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution d1 = new Distribution("BetweenessCentrality",
				getDistribution(this.bC));
		return new Distribution[] { d1 };

	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	private double[] getDistribution(
			HashMap<DirectedNode, Double> betweeneesCentralityScore2) {
		double[] temp = new double[betweeneesCentralityScore2.size()];
		int counter = 0;
		for (DirectedNode i : betweeneesCentralityScore2.keySet()) {
			temp[counter] = betweeneesCentralityScore2.get(i);
			counter++;
		}
		return temp;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof DirectedBetweenessCentrality;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
