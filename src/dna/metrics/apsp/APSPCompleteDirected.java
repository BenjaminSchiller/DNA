package dna.metrics.apsp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

@SuppressWarnings("rawtypes")
public abstract class APSPCompleteDirected extends Metric {

	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parentsOut;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsOut;

	public APSPCompleteDirected(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			DirectedNode n = (DirectedNode) ie;
			buildTrees(n);
		}
		return true;

	}

	protected void buildTrees(DirectedNode n) {
		HashMap<DirectedNode, DirectedNode> parent = new HashMap<DirectedNode, DirectedNode>();
		HashMap<DirectedNode, Integer> height = new HashMap<DirectedNode, Integer>();

		for (IElement ie : g.getNodes()) {
			DirectedNode t = (DirectedNode) ie;
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
			for (IElement ie : current.getOutgoingEdges()) {
				DirectedEdge e = (DirectedEdge) ie;
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
	public void init_() {
		this.parentsOut = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heightsOut = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
	}

	@Override
	public void reset_() {
		this.parentsOut = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heightsOut = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
	}

	@Override
	public Value[] getValues() {
		return new Value[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Distribution[] getDistributions() {
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
