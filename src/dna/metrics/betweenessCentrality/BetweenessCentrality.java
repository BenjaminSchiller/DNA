package dna.metrics.betweenessCentrality;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class BetweenessCentrality extends Metric {

	protected HashMap<UndirectedNode, Double> betweeneesCentralityScore;
	protected HashMap<UndirectedNode, HashMap<UndirectedNode, ShortestPathTreeElement>> shortestPathTrees;

	public BetweenessCentrality(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		this.shortestPathTrees = new HashMap<>();
		this.betweeneesCentralityScore = new HashMap<>();
	}

	@Override
	public void reset_() {
		this.shortestPathTrees = new HashMap<>();
		this.betweeneesCentralityScore = new HashMap<>();
	}

	@Override
	public boolean compute() {
		Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();
		Stack<UndirectedNode> s = new Stack<UndirectedNode>();

		for (IElement ie : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) ie;
			betweeneesCentralityScore.put(t, 0d);
		}

		for (IElement ie : g.getNodes()) {
			UndirectedNode n = (UndirectedNode) ie;
			// stage ONE
			s.clear();
			q.clear();
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPath = new HashMap<UndirectedNode, ShortestPathTreeElement>();

			for (IElement ieE : g.getNodes()) {
				UndirectedNode t = (UndirectedNode) ieE;
				if (t == n) {
					ShortestPathTreeElement temp = new ShortestPathTreeElement(
							t.getIndex());
					temp.setDistanceToRoot(0);
					temp.setShortestPathCount(1);
					shortestPath.put(t, temp);
				} else {
					ShortestPathTreeElement temp = new ShortestPathTreeElement(
							t.getIndex());
					shortestPath.put(t, temp);
				}
			}

			q.add(n);

			// stage 2
			while (!q.isEmpty()) {
				UndirectedNode v = q.poll();
				s.push(v);
				ShortestPathTreeElement vTE = shortestPath.get(v);

				for (IElement iEdges : v.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) iEdges;
					UndirectedNode w = edge.getDifferingNode(v);
					ShortestPathTreeElement wTE = shortestPath.get(w);

					if (wTE.getDistanceToRoot() == Integer.MAX_VALUE) {
						q.add(w);
						wTE.setDistanceToRoot(vTE.getDistanceToRoot() + 1);
					}
					if (wTE.getDistanceToRoot() == vTE.getDistanceToRoot() + 1) {
						wTE.setShortestPathCount(wTE.getShortestPathCount()
								+ vTE.getShortestPathCount());
						wTE.addParent(v);
					}
				}
			}

			// stage 3

			while (!s.isEmpty()) {
				UndirectedNode w = s.pop();
				ShortestPathTreeElement wTE = shortestPath.get(w);
				for (UndirectedNode parent : wTE.getParents()) {
					ShortestPathTreeElement pTE = shortestPath.get(parent);

					double sumForCurretConnection = pTE.getShortestPathCount()
							* (1 + wTE.getAccumulativSum())
							/ wTE.getShortestPathCount();
					pTE.setAccumulativSum(pTE.getAccumulativSum()
							+ sumForCurretConnection);
				}
				if (w != n) {
					double currentScore = this.betweeneesCentralityScore.get(w);
					this.betweeneesCentralityScore.put(w,
							currentScore + wTE.getAccumulativSum());
				}
			}

			this.shortestPathTrees.put(n, shortestPath);
		}

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof BetweenessCentrality)) {
			return false;
		}
		boolean success = true;
		BetweenessCentrality bc = (BetweenessCentrality) m;
		for (IElement ie : g.getNodes()) {
			UndirectedNode n = (UndirectedNode) ie;
			if (Math.abs(this.betweeneesCentralityScore.get(n).doubleValue()
					- bc.betweeneesCentralityScore.get(n).doubleValue()) > 0.0001) {
				// System.out.println("diff at Node n " + n + " expected Score "
				// + this.betweeneesCentralityScore.get(n) + " is "
				// + bc.betweeneesCentralityScore.get(n));
				success = false;
			}

		}

		for (IElement ie1 : g.getNodes()) {
			UndirectedNode n1 = (UndirectedNode) ie1;
			for (IElement ie2 : g.getNodes()) {
				UndirectedNode n2 = (UndirectedNode) ie2;
				if (this.shortestPathTrees.get(n1).get(n2)
						.getShortestPathCount() != bc.shortestPathTrees.get(n1)
						.get(n2).getShortestPathCount()) {
					System.out.println("diff at Tree "
							+ n1
							+ "in Node n "
							+ n2
							+ " expected SPC "
							+ this.shortestPathTrees.get(n1).get(n2)
									.getShortestPathCount()
							+ " is "
							+ bc.shortestPathTrees.get(n1).get(n2)
									.getShortestPathCount());
					success = false;
				}
				if (Math.abs(this.shortestPathTrees.get(n1).get(n2)
						.getAccumulativSum()
						- bc.shortestPathTrees.get(n1).get(n2)
								.getAccumulativSum()) > 0.000001) {
					System.out.println("diff at Tree "
							+ n1
							+ "in Node n "
							+ n2
							+ " expected Sum "
							+ this.shortestPathTrees.get(n1).get(n2)
									.getAccumulativSum()
							+ " is "
							+ bc.shortestPathTrees.get(n1).get(n2)
									.getAccumulativSum()
							+ " height == "
							+ bc.shortestPathTrees.get(n1).get(n2)
									.getDistanceToRoot());
					success = false;
				}

				if (this.shortestPathTrees.get(n1).get(n2).getDistanceToRoot() != bc.shortestPathTrees
						.get(n1).get(n2).getDistanceToRoot()) {
					System.out.println("diff at Tree "
							+ n1
							+ "in Node n "
							+ n2
							+ " expected dist "
							+ this.shortestPathTrees.get(n1).get(n2)
									.getDistanceToRoot()
							+ " is "
							+ bc.shortestPathTrees.get(n1).get(n2)
									.getDistanceToRoot());
					success = false;
				}

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
				getDistribution(this.betweeneesCentralityScore));
		return new Distribution[] { d1 };

	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	private double[] getDistribution(
			HashMap<UndirectedNode, Double> betweeneesCentralityScore2) {
		double[] temp = new double[betweeneesCentralityScore2.size()];
		int counter = 0;
		for (UndirectedNode i : betweeneesCentralityScore2.keySet()) {
			temp[counter] = betweeneesCentralityScore2.get(i);
			counter++;
		}
		return temp;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof BetweenessCentrality;
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
