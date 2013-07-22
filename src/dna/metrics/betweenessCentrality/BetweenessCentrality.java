package dna.metrics.betweenessCentrality;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

public abstract class BetweenessCentrality extends Metric {

	protected int[] shortesPathCount;
	protected int[] distanceToRoot;
	protected double[] betweeneesCentralityScore;
	protected List<Node>[] parentVertices;

	public BetweenessCentrality(String name, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		super(name, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
	}

	@Override
	protected void init(Graph g) {
		this.shortesPathCount = new int[this.g.getNodes().length];
		this.distanceToRoot = new int[this.g.getNodes().length];
		this.betweeneesCentralityScore = new double[this.g.getNodes().length];
		this.parentVertices = new List[this.g.getNodes().length];
	}

	@Override
	public void reset_() {
		this.shortesPathCount = new int[this.g.getNodes().length];
		this.distanceToRoot = new int[this.g.getNodes().length];
		this.betweeneesCentralityScore = new double[this.g.getNodes().length];
		this.parentVertices = new List[this.g.getNodes().length];
	}

	@Override
	protected boolean compute_() {
		Queue<Node> q = new LinkedList<Node>();
		Stack<Node> s = new Stack<Node>();
		for (Node n : this.g.getNodes()) {
			// stage ONE
			// TODO:Stage One Passt Noch Nicht
			s.clear();
			q.clear();
			this.parentVertices[n.getIndex()] = new LinkedList<Node>();
			this.shortesPathCount[n.getIndex()] = 1;
			this.distanceToRoot[n.getIndex()] = -1;
			q.add(n);

			// stage 2
			while (!q.isEmpty()) {
				Node v = q.poll();
				s.push(v);
				for (Node neighbour : v.getNeighbors()) {
					if (distanceToRoot[neighbour.getIndex()] != -1) {
						q.add(neighbour);
						distanceToRoot[neighbour.getIndex()] = distanceToRoot[v
								.getIndex()] + 1;
					}
					if (distanceToRoot[neighbour.getIndex()] == distanceToRoot[v
							.getIndex()] + 1) {
						this.shortesPathCount[neighbour.getIndex()] = this.shortesPathCount[neighbour
								.getIndex()]
								+ this.shortesPathCount[v.getIndex()];
						this.parentVertices[neighbour.getIndex()].add(v);
					}
				}
			}

			// stage 3
			double[] temp = new double[this.g.getNodes().length];
			while (!s.isEmpty()) {
				Node w = s.pop();
				for (Node parent : this.parentVertices[w.getIndex()]) {
					temp[parent.getIndex()] = temp[parent.getIndex()]
							+ this.shortesPathCount[parent.getIndex()]
							/ this.shortesPathCount[w.getIndex()]
							* (1 + temp[w.getIndex()]);
				}
				if (w != n) {
					this.betweeneesCentralityScore[w.getIndex()] = this.betweeneesCentralityScore[w
							.getIndex()] + temp[w.getIndex()];
				}
			}
		}

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		return false;
	}

	@Override
	public boolean cleanupApplication() {
		return false;
	}

	@Override
	protected Value[] getValues() {
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("BetweenessCentrality",
				this.betweeneesCentralityScore);
		return new Distribution[] { d1 };

	}

}
