package dna.metrics.betweenessCentrality;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

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
public abstract class BetweenessCentrality extends Metric {

	protected HashMap<Integer, Double> betweeneesCentralityScore;

	protected HashMap<Integer, HashMap<Integer, ShortestPathTreeElement>> shortestPathTrees;

	public BetweenessCentrality(String name, ApplicationType type) {
		super(name, type);
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
		UndirectedGraph g = (UndirectedGraph) this.g;
		for (UndirectedNode n : g.getNodes()) {
			// stage ONE
			// TODO:Stage One Passt Noch Nicht
			s.clear();
			q.clear();
			HashMap<Integer, ShortestPathTreeElement> shortestPath = new HashMap<Integer, ShortestPathTreeElement>();

			ShortestPathTreeElement root = new ShortestPathTreeElement(
					n.getIndex());
			root.setDistanceToRoot(0);
			root.setShortestPathCount(1);
			shortestPath.put(n.getIndex(), root);
			q.add(n);

			// stage 2
			while (!q.isEmpty()) {
				UndirectedNode v = q.poll();

				s.push(v);
				for (UndirectedEdge ed : v.getEdges()) {
					UndirectedNode neighbour = ed.getNode1();
					if (neighbour == v)
						neighbour = ed.getNode2();
					if (!shortestPath.containsKey(neighbour.getIndex())) {
						q.add(neighbour);
						ShortestPathTreeElement temp = new ShortestPathTreeElement(
								neighbour.getIndex());
						temp.setDistanceToRoot(shortestPath.get(v.getIndex())
								.getDistanceToRoot() + 1);
					}
					if (shortestPath.get(neighbour.getIndex())
							.getDistanceToRoot() == shortestPath.get(
							v.getIndex()).getDistanceToRoot() + 1) {
						shortestPath
								.get(neighbour.getIndex())
								.setShortestPathCount(
										shortestPath.get(neighbour.getIndex())
												.getShortestPathCount()
												+ shortestPath
														.get(v.getIndex())
														.getShortestPathCount());
						shortestPath.get(neighbour).addParent(
								shortestPath.get(v.getIndex()));

					}
				}
			}

			// stage 3

			while (!s.isEmpty()) {
				UndirectedNode w = s.pop();
				for (ShortestPathTreeElement parent : shortestPath.get(
						w.getIndex()).getParents()) {
					parent.setAccumulativSum(parent.getAccumulativSum()
							+ parent.getShortestPathCount()
							/ shortestPath.get(w.getIndex())
									.getShortestPathCount()
							* (1 + shortestPath.get(w.getIndex())
									.getAccumulativSum()));
				}
				if (w != n) {
					this.betweeneesCentralityScore.put(w.getIndex(),
							this.betweeneesCentralityScore.get(w.getIndex())
									+ shortestPath.get(w.getIndex())
											.getAccumulativSum());
				}
			}

			this.shortestPathTrees.put(n.getIndex(), shortestPath);
		}

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		return false;
	}

	@Override
	protected Value[] getValues() {
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("BetweenessCentrality",
				getDistribution(this.betweeneesCentralityScore));
		return new Distribution[] { d1 };

	}

	private double[] getDistribution(
			HashMap<Integer, Double> betweeneesCentralityScore2) {
		double[] temp = new double[betweeneesCentralityScore2.size()];
		int counter = 0;
		for (int i : betweeneesCentralityScore2.keySet()) {
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
