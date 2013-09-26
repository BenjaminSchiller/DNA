package dna.metrics.apsp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class APSPCompleteUndirected extends Metric {

	protected HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>> parentsOut;
	protected HashMap<UndirectedNode, HashMap<UndirectedNode, Integer>> heightsOut;

	public APSPCompleteUndirected(String name, ApplicationType type) {
		super(name, type);

	}

	@Override
	public boolean compute() {

		UndirectedGraph g = (UndirectedGraph) this.g;

		for (UndirectedNode n : g.getNodes()) {
			buildTrees(g, n);
		}

		return true;

	}

	protected void buildTrees(UndirectedGraph g, UndirectedNode n) {
		HashMap<UndirectedNode, UndirectedNode> parentOut = new HashMap<>();
		HashMap<UndirectedNode, Integer> heightOut = new HashMap<>();

		for (UndirectedNode t : g.getNodes()) {
			if (t.equals(n)) {
				heightOut.put(n, 0);
			} else {
				heightOut.put(t, Integer.MAX_VALUE);
			}
		}

		Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();
		q.add(n);
		while (!q.isEmpty()) {
			UndirectedNode current = q.poll();
			for (UndirectedEdge e : current.getEdges()) {
				UndirectedNode t = e.getDifferingNode(current);

				if (heightOut.get(t) != Integer.MAX_VALUE) {
					continue;
				}
				heightOut.put(t, heightOut.get(current) + 1);
				parentOut.put(t, current);

				q.add(t);

			}
		}

		this.heightsOut.put(n, heightOut);
		this.parentsOut.put(n, parentOut);
	}

	@Override
	protected void init_() {
		this.parentsOut = new HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>>();
		this.heightsOut = new HashMap<UndirectedNode, HashMap<UndirectedNode, Integer>>();
	}

	@Override
	public void reset_() {
		this.parentsOut = new HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>>();
		this.heightsOut = new HashMap<UndirectedNode, HashMap<UndirectedNode, Integer>>();
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
			HashMap<UndirectedNode, HashMap<UndirectedNode, Integer>> heightsOut2) {

		return new double[] {};
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof APSPCompleteUndirected)) {
			return false;
		}
		boolean success = true;
		APSPCompleteUndirected apsp = (APSPCompleteUndirected) m;

		for (UndirectedNode n1 : heightsOut.keySet()) {
			for (UndirectedNode n2 : heightsOut.get(n1).keySet()) {
				if (this.heightsOut.get(n1).get(n2).intValue() != apsp.heightsOut
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
		return m != null && m instanceof APSPCompleteUndirected;
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
