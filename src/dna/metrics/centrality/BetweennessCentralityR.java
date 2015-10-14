package dna.metrics.centrality;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distr2.BinnedDoubleDistr;
import dna.series.data.nodevaluelists.NodeValueList;

public class BetweennessCentralityR extends BetweennessCentrality implements
		IRecomputation {

	public BetweennessCentralityR() {
		super("BetweennessCentralityR");
	}

	@Override
	public boolean recompute() {

		this.bCC = new NodeValueList("BC_Score",
				new double[this.g.getMaxNodeIndex() + 1]);
		this.binnedBC = new BinnedDoubleDistr("Normalized-BC", 0.01d);
		this.bCSum = 0d;
		this.sumShortestPaths = 0;

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
						DirectedNode w = (DirectedNode) edge
								.getDifferingNode(v);

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
						UndirectedNode w = (UndirectedNode) edge
								.getDifferingNode(v);

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
			while (!s.isEmpty()) {
				Node w = s.pop();
				for (Node parent : p.get(w)) {
					double sumForCurretConnection = spc.get(parent)
							* (1 + sums.get(w)) / spc.get(w);
					sums.put(parent, sums.get(parent) + sumForCurretConnection);
				}
				if (w != n) {
					double currentScore = this.bCC.getValue(w.getIndex());
					this.bCC.setValue(w.getIndex(), currentScore + sums.get(w));
					this.bCSum += sums.get(w) - 3;
				}
			}

			sumShortestPaths += sumSPFromHM(spc, n);

		}
		return true;
	}

}
