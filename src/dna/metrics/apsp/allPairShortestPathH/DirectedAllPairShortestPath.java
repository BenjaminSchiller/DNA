package dna.metrics.apsp.allPairShortestPathH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metrics.apsp.Triplet;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class DirectedAllPairShortestPath extends Metric {

	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parentsOut;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsOut;
	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parentsIn;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsIn;
	protected HashMap<Triplet<DirectedNode, DirectedNode, Integer>, Integer> count;
	protected HashMap<Triplet<DirectedNode, DirectedNode, Integer>, ArrayList<DirectedNode>> list;

	protected int d;

	public DirectedAllPairShortestPath(String name, ApplicationType type) {
		super(name, type, MetricType.heuristic);
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
		HashMap<DirectedNode, DirectedNode> parentOut = new HashMap<>();
		HashMap<DirectedNode, DirectedNode> parentIn = new HashMap<>();
		HashMap<DirectedNode, Integer> heightIn = new HashMap<>();
		HashMap<DirectedNode, Integer> heightOut = new HashMap<>();

		for (IElement ie : g.getNodes()) {
			DirectedNode t = (DirectedNode) ie;
			if (t.equals(n)) {
				heightIn.put(n, 0);
				heightOut.put(n, 0);
			} else {
				heightIn.put(t, Integer.MAX_VALUE);
				heightOut.put(t, Integer.MAX_VALUE);
			}
		}

		Queue<DirectedNode> q = new LinkedList<DirectedNode>();
		q.add(n);
		while (!q.isEmpty()) {
			DirectedNode current = q.poll();
			for (IElement ie : current.getOutgoingEdges()) {
				DirectedEdge e = (DirectedEdge) ie;
				if (heightOut.get(e.getDst()) != Integer.MAX_VALUE) {
					continue;
				}
				heightOut.put(e.getDst(), heightOut.get(current) + 1);
				parentOut.put(e.getDst(), current);
				if (heightOut.get(e.getDst()) < this.d) {
					q.add(e.getDst());
				}
			}
		}

		q.add(n);
		while (!q.isEmpty()) {
			DirectedNode current = q.poll();
			for (IElement ie : current.getIncomingEdges()) {
				DirectedEdge e = (DirectedEdge) ie;
				if (heightOut.get(e.getSrc()) != Integer.MAX_VALUE) {
					continue;
				}
				heightOut.put(e.getSrc(), heightOut.get(current) + 1);
				parentOut.put(e.getSrc(), current);
				if (heightOut.get(e.getSrc()) < this.d) {
					q.add(e.getSrc());
				}
			}
		}

		buildCountAndList(n, parentOut, parentIn, heightIn, heightOut);

		this.heightsIn.put(n, heightIn);
		this.heightsOut.put(n, heightOut);
		this.parentsIn.put(n, parentIn);
		this.parentsOut.put(n, parentOut);
	}

	private void buildCountAndList(DirectedNode n,
			HashMap<DirectedNode, DirectedNode> parentOut,
			HashMap<DirectedNode, DirectedNode> parentIn,
			HashMap<DirectedNode, Integer> heightIn,
			HashMap<DirectedNode, Integer> heightOut) {
		for (DirectedNode n1 : parentIn.keySet()) {

			for (DirectedNode n2 : parentOut.keySet()) {
				if (n1 != n2) {

					Triplet<DirectedNode, DirectedNode, Integer> t = new Triplet<DirectedNode, DirectedNode, Integer>(
							n1, n2, heightIn.get(n1) + heightOut.get(n2));
					if (count.containsKey(t)) {
						this.count.put(t, this.count.get(t) + 1);
					} else {
						this.count.put(t, 1);
					}
					if (list.containsKey(t)) {
						this.list.get(t).add(n);
					} else {
						ArrayList<DirectedNode> temp = new ArrayList<DirectedNode>();
						temp.add(n);
						this.list.put(t, temp);
					}
				}

			}

		}
	}

	@Override
	public void init_() {
		this.parentsIn = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.parentsOut = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heightsIn = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
		this.heightsOut = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
		this.count = new HashMap<Triplet<DirectedNode, DirectedNode, Integer>, Integer>();
		this.list = new HashMap<Triplet<DirectedNode, DirectedNode, Integer>, ArrayList<DirectedNode>>();
	}

	@Override
	public void reset_() {
		this.parentsIn = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.parentsOut = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heightsIn = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
		this.heightsOut = new HashMap<DirectedNode, HashMap<DirectedNode, Integer>>();
		this.count = new HashMap<Triplet<DirectedNode, DirectedNode, Integer>, Integer>();
		this.list = new HashMap<Triplet<DirectedNode, DirectedNode, Integer>, ArrayList<DirectedNode>>();
	}

	@Override
	public Value[] getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Distribution[] getDistributions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof DirectedAllPairShortestPath;
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
