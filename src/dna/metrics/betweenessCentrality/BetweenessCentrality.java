package dna.metrics.betweenessCentrality;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

public abstract class BetweenessCentrality extends Metric {

	protected int[] shortesPathCount;
	protected int[] distanceToRoot;
	protected double[] betweeneesCentralityScore;
	protected List<UndirectedNode>[] parentVertices;

	public BetweenessCentrality(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init() {
		this.shortesPathCount = new int[this.g.getNodes().size()];
		this.distanceToRoot = new int[this.g.getNodes().size()];
		this.betweeneesCentralityScore = new double[this.g.getNodes().size()];
		this.parentVertices = new List[this.g.getNodes().size()];
	}

	@Override
	public void reset_() {
		this.shortesPathCount = new int[this.g.getNodes().size()];
		this.distanceToRoot = new int[this.g.getNodes().size()];
		this.betweeneesCentralityScore = new double[this.g.getNodes().size()];
		this.parentVertices = new List[this.g.getNodes().size()];
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
			this.parentVertices[n.getIndex()] = new LinkedList<UndirectedNode>();
			this.shortesPathCount[n.getIndex()] = 1;
			this.distanceToRoot[n.getIndex()] = -1;
			q.add(n);

			// stage 2
			while (!q.isEmpty()) {
				UndirectedNode v = q.poll();
				s.push(v);
				for (UndirectedEdge ed : v.getEdges()) {
					UndirectedNode neighbour = ed.getNode1();
					if (neighbour == v)
						neighbour = ed.getNode2();
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
			double[] temp = new double[this.g.getNodes().size()];
			while (!s.isEmpty()) {
				UndirectedNode w = s.pop();
				for (UndirectedNode parent : this.parentVertices[w.getIndex()]) {
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
