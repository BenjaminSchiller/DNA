package dna.metrics.apsp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class APSPCompleteDirected extends Metric {

	protected int zähl;
	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parentsOut;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsOut;

	public APSPCompleteDirected(String name, ApplicationType type) {
		super(name, type);

	}

	@Override
	public boolean compute() {

		DirectedGraph g = (DirectedGraph) this.g;

		for (DirectedNode n : g.getNodes()) {
			buildTrees(g, n);
		}
		return true;

	}

	protected void buildTrees(DirectedGraph g, DirectedNode n) {
		HashMap<DirectedNode, DirectedNode> parent = new HashMap<DirectedNode, DirectedNode>();
		HashMap<DirectedNode, Integer> height = new HashMap<DirectedNode, Integer>();

		for (DirectedNode t : g.getNodes()) {
			if (t.equals(n)) {
				height.put(n, 0);
			} else {
				height.put(t, Integer.MAX_VALUE);
			}
		}

		Queue<DirectedNode> q = new LinkedList<DirectedNode>();
		q.add(n);
		while (!q.isEmpty()) {
			DirectedNode current = q.poll();
			this.zähl++;
			for (DirectedEdge e : current.getOutgoingEdges()) {
				if (height.get(e.getDst()) != Integer.MAX_VALUE) {
					continue;
				}
				height.put(e.getDst(), height.get(current) + 1);
				parent.put(e.getDst(), current);
				q.add(e.getDst());
			}
		}

		this.heightsOut.put(n, height);
		this.parentsOut.put(n, parent);
	}

	@Override
	protected void init_() {
		this.parentsOut = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heightsOut = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
		this.zähl = 0;
	}

	@Override
	public void reset_() {
		this.parentsOut = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heightsOut = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
		this.zähl = 0;
	}

	@Override
	protected Value[] getValues() {
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("APSP heights",
				getDistribution(this.heightsOut));
		return new Distribution[] { d1 };
	}

	private double[] getDistribution(
			HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsOut2) {

		return new double[] {};
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof APSPCompleteDirected)) {
			return false;
		}
		boolean success = true;
		APSPCompleteDirected apsp = (APSPCompleteDirected) m;

		System.out.println(this + " " + this.zähl + " " + apsp + " "
				+ apsp.zähl);
		for (DirectedNode n1 : heightsOut.keySet()) {
			for (DirectedNode n2 : heightsOut.get(n1).keySet()) {
				if (this.heightsOut.get(n1).get(n2).intValue() < apsp.heightsOut
						.get(n1).get(n2).intValue()) {
					success = false;
					System.err.println("Diff @ Height for Node " + n2
							+ " in Tree " + n1 + " expected "
							+ this.heightsOut.get(n1).get(n2) + " is "
							+ apsp.heightsOut.get(n1).get(n2));
				}
				if (this.heightsOut.get(n1).get(n2).intValue() > apsp.heightsOut
						.get(n1).get(n2).intValue()) {
					success = false;
					System.out.println("Diff @ Height for Node " + n2
							+ " in Tree " + n1 + " expected "
							+ this.heightsOut.get(n1).get(n2) + " is "
							+ apsp.heightsOut.get(n1).get(n2));
				}
			}

		}

		return success;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof APSPCompleteDirected;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

}
