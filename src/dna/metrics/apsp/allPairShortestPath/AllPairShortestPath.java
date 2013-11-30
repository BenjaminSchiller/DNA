package dna.metrics.apsp.allPairShortestPath;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class AllPairShortestPath extends Metric {

	protected HashMap<Node, HashMap<Node, Node>> parents;
	protected HashMap<Node, HashMap<Node, Integer>> heights;
	protected DistributionInt dists;
	protected int sum;

	public AllPairShortestPath(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			Node n = (Node) ie;
			buildTrees(n);
		}

		return true;

	}

	protected void buildTrees(Node n) {
		HashMap<Node, Node> parent = new HashMap<>();
		HashMap<Node, Integer> height = new HashMap<>();

		for (IElement ie : g.getNodes()) {
			Node t = (Node) ie;
			if (t.equals(n)) {
				height.put(n, 0);
			} else {
				height.put(t, Integer.MAX_VALUE);
			}
		}

		Queue<Node> q = new LinkedList<Node>();
		q.add(n);

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			while (!q.isEmpty()) {
				DirectedNode current = (DirectedNode) q.poll();
				for (IElement ie : current.getOutgoingEdges()) {
					DirectedEdge e = (DirectedEdge) ie;
					if (height.get(e.getDst()) != Integer.MAX_VALUE) {
						continue;
					}
					height.put(e.getDst(), height.get(current) + 1);
					parent.put(e.getDst(), current);
					dists.incr(height.get(e.getDst()));
					sum += height.get(e.getDst());
					q.add(e.getDst());
				}
			}
		} else {

			while (!q.isEmpty()) {
				Node current = q.poll();
				for (IElement iEdge : current.getEdges()) {
					UndirectedEdge e = (UndirectedEdge) iEdge;
					Node t = e.getDifferingNode(current);

					if (height.get(t) != Integer.MAX_VALUE) {
						continue;
					}
					height.put(t, height.get(current) + 1);
					parent.put(t, current);
					dists.incr(height.get(t));
					q.add(t);
					sum += height.get(t);
				}
			}
		}
		this.heights.put(n, height);
		this.parents.put(n, parent);
	}

	@Override
	public void init_() {
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Integer>>();
		this.dists = new DistributionInt("ShortestPathDist");
		this.sum = 0;
	}

	@Override
	public void reset_() {
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Integer>>();
		this.dists = new DistributionInt("ShortestPathDist");
		this.sum = 0;
	}

	@Override
	public Value[] getValues() {
		double avg1;
		double avg2;
		double dia;
		if (g.getNodeCount() != 0) {
			dists.truncate();
			double sum1 = getSum();
			avg1 = sum1 / (double) dists.getDenominator();
			avg2 = sum1 / (double) (g.getNodeCount() * (g.getNodeCount() - 1));
			dia = this.dists.getMax();
		} else {
			avg1 = 0d;
			avg2 = 0d;
			dia = 0d;
		}

		Value v1 = new Value("avg_shortest_path_Number_Existing_Paths", avg1);
		Value v2 = new Value("diameter", dia);
		Value v3 = new Value("avg_shortest_path_Number_Possible_Paths", avg2);
		return new Value[] { v1, v2, v3 };
	}

	private double getSum() {
		double s = 0d;
		int[] v = dists.getIntValues();
		for (int i = 0; i < v.length; i++) {
			s += v[i];
		}
		return s;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution[] result;
		if (g.getNodeCount() != 0) {
			this.dists.truncate();
			result = new Distribution[] { dists };
		} else {
			result = new Distribution[0];
		}

		// int i = 0;
		// for (Node n : heightsOut.keySet()) {
		// result[i] = new Distribution("distsForNode_" + n.getIndex(),
		// getDistribution(this.heightsOut.get(n)));
		// i++;
		// }
		return result;
	}

	private double[] getDistribution(HashMap<Node, Integer> hashMap) {
		double[] result = new double[this.g.getMaxNodeIndex() + 1];
		for (Node d : hashMap.keySet()) {
			result[d.getIndex()] = hashMap.get(d);
		}
		return result;
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof AllPairShortestPath)) {
			return false;
		}
		boolean success = true;
		AllPairShortestPath apsp = (AllPairShortestPath) m;

		for (Node n1 : heights.keySet()) {
			for (Node n2 : heights.get(n1).keySet()) {
				if (this.heights.get(n1).get(n2).intValue() != apsp.heights
						.get(n1).get(n2).intValue()) {
					success = false;
					System.out.println("Diff @ Height for Node " + n2
							+ " in Tree " + n1 + " expected "
							+ this.heights.get(n1).get(n2) + " is "
							+ apsp.heights.get(n1).get(n2));
				}
			}

		}

		return success;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof AllPairShortestPath;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return Node.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return Node.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
