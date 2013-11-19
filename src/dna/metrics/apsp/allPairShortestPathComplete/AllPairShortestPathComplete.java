package dna.metrics.apsp.allPairShortestPathComplete;

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
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class AllPairShortestPathComplete extends Metric {

	protected HashMap<Node, HashMap<Node, Node>> parentsOut;
	protected HashMap<Node, HashMap<Node, Integer>> heightsOut;

	public AllPairShortestPathComplete(String name,
			ApplicationType type) {
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
		HashMap<Node, Node> parentOut = new HashMap<>();
		HashMap<Node, Integer> heightOut = new HashMap<>();

		for (IElement ie : g.getNodes()) {
			Node t = (Node) ie;
			if (t.equals(n)) {
				heightOut.put(n, 0);
			} else {
				heightOut.put(t, Integer.MAX_VALUE);
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
					if (heightOut.get(e.getDst()) != Integer.MAX_VALUE) {
						continue;
					}
					heightOut.put(e.getDst(), heightOut.get(current) + 1);
					parentOut.put(e.getDst(), current);
					q.add(e.getDst());
				}
			}
		} else {

			while (!q.isEmpty()) {
				Node current = q.poll();
				for (IElement iEdge : current.getEdges()) {
					UndirectedEdge e = (UndirectedEdge) iEdge;
					Node t = e.getDifferingNode(current);

					if (heightOut.get(t) != Integer.MAX_VALUE) {
						continue;
					}
					heightOut.put(t, heightOut.get(current) + 1);
					parentOut.put(t, current);

					q.add(t);

				}
			}
		}
		this.heightsOut.put(n, heightOut);
		this.parentsOut.put(n, parentOut);
	}

	@Override
	public void init_() {
		this.parentsOut = new HashMap<Node, HashMap<Node, Node>>();
		this.heightsOut = new HashMap<Node, HashMap<Node, Integer>>();
	}

	@Override
	public void reset_() {
		this.parentsOut = new HashMap<Node, HashMap<Node, Node>>();
		this.heightsOut = new HashMap<Node, HashMap<Node, Integer>>();
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
		Distribution d1 = new Distribution("APSP heights",
				getDistribution(this.heightsOut));
		return new Distribution[] { d1 };
	}

	private double[] getDistribution(
			HashMap<Node, HashMap<Node, Integer>> heightsOut2) {

		return new double[] {};
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof AllPairShortestPathComplete)) {
			return false;
		}
		boolean success = true;
		AllPairShortestPathComplete apsp = (AllPairShortestPathComplete) m;

		for (Node n1 : heightsOut.keySet()) {
			for (Node n2 : heightsOut.get(n1).keySet()) {
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
		return m != null && m instanceof AllPairShortestPathComplete;
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
