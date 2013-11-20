package dna.metrics.old;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class DirectedAllPairShortestPathComplete extends Metric {

	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parentsOut;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsOut;

	public DirectedAllPairShortestPathComplete(String name, ApplicationType type) {
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
		return new NodeValueList[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution[] result = new Distribution[this.heightsOut.size()];
		int i = 0;
		for (DirectedNode n : heightsOut.keySet()) {
			result[i] = new Distribution("distsForNode_" + n.getIndex(),
					getDistribution(this.heightsOut.get(n)));
			i++;
		}
		return result;
	}

	private double[] getDistribution(HashMap<DirectedNode, Integer> hashMap) {
		double[] result = new double[this.g.getMaxNodeIndex() + 1];
		for (DirectedNode d : hashMap.keySet()) {
			result[d.getIndex()] = hashMap.get(d);
		}
		return result;
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof DirectedAllPairShortestPathComplete)) {
			return false;
		}
		boolean success = true;
		DirectedAllPairShortestPathComplete apsp = (DirectedAllPairShortestPathComplete) m;
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
		return m != null && m instanceof DirectedAllPairShortestPathComplete;
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
