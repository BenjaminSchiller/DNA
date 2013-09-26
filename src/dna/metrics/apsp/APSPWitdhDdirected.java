package dna.metrics.apsp;

import java.util.ArrayList;
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
public abstract class APSPWitdhDdirected extends Metric {

	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parentsOut;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsOut;
	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parentsIn;
	protected HashMap<DirectedNode, HashMap<DirectedNode, Integer>> heightsIn;
	protected HashMap<Triplet<DirectedNode, DirectedNode, Integer>, Integer> count;
	protected HashMap<Triplet<DirectedNode, DirectedNode, Integer>, ArrayList<DirectedNode>> list;

	protected int d;

	public APSPWitdhDdirected(String name, ApplicationType type) {
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
		HashMap<DirectedNode, DirectedNode> parentOut = new HashMap<>();
		HashMap<DirectedNode, DirectedNode> parentIn = new HashMap<>();
		HashMap<DirectedNode, Integer> heightIn = new HashMap<>();
		HashMap<DirectedNode, Integer> heightOut = new HashMap<>();

		for (DirectedNode t : g.getNodes()) {
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
			for (DirectedEdge e : current.getOutgoingEdges()) {
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
			for (DirectedEdge e : current.getIncomingEdges()) {
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
	protected void init_() {
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
	protected Value[] getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Distribution[] getDistributions() {
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
		return m != null && m instanceof APSPWitdhDdirected;
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
