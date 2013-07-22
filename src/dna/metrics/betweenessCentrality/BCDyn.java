package dna.metrics.betweenessCentrality;

import java.util.LinkedList;
import java.util.Queue;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;

public class BCDyn extends BetweenessCentrality {

	public BCDyn() {
		super("BCDyn", false, true, false);
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("before diff");
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e) {
		Node src = e.getSrc();
		Node dst = e.getDst();

		Queue<Node> qBFS = new LinkedList<Node>();
		// TODO:Levels einrichten
		Queue<Node>[] qLevel = new Queue[this.g.getNodes().length];
		int[] distanceP = new int[this.g.getNodes().length];
		boolean[] visited = new boolean[this.g.getNodes().length];
		int[] shortestPaths = this.shortesPathCount;

		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<Node>();
		}
		// Stage 2
		while (!qBFS.isEmpty()) {
			Node v = qBFS.poll();
			for (Node n : v.getNeighbors()) {
				if (this.distanceToRoot[n.getIndex()] == this.distanceToRoot[v
						.getIndex()] + 1) {
					if (!visited[n.getIndex()]) {
						qBFS.add(n);
						qLevel[this.distanceToRoot[n.getIndex()]].add(n);
						visited[n.getIndex()] = true;
						this.distanceToRoot[n.getIndex()] = this.distanceToRoot[v
								.getIndex()] + 1;
						distanceP[n.getIndex()] = distanceP[v.getIndex()];
					} else {
						distanceP[n.getIndex()] = distanceP[n.getIndex()]
								+ distanceP[v.getIndex()];
					}
					shortestPaths[n.getIndex()] = shortestPaths[n.getIndex()]
							+ distanceP[v.getIndex()];
				}
			}
		}

		// Stage 3
		double[] temp = new double[this.g.getNodes().length];
		for (int i = qLevel.length - 1; i >= 0; i--) {
			while (!qLevel[i].isEmpty()) {
				Node w = qLevel[i].poll();
				for (Node n : this.parentVertices[w.getIndex()]) {
					if (!visited[n.getIndex()]) {
						qLevel[i - 1].add(n);
						visited[n.getIndex()] = true;
						// TODO:aus betweeneesCentrality sichtbar amchen
						temp[n.getIndex()] = 0000;
					}
					temp[n.getIndex()] = temp[n.getIndex()]
							+ shortestPaths[n.getIndex()]
							/ shortestPaths[w.getIndex()]
							* (1 + temp[w.getIndex()]);
					if (visited[n.getIndex()] && (n != src || w != dst)) {
						temp[n.getIndex()] = temp[n.getIndex()]
								- this.shortesPathCount[n.getIndex()]
								/ this.shortesPathCount[w.getIndex()]
								* (1 + temp[w.getIndex()]);
						// TODO:das array aus betweeness nehmen selbe wie oben
					}
					// TODO:root
					if (w != n) {
						this.betweeneesCentralityScore[w.getIndex()] = this.betweeneesCentralityScore[w
								.getIndex()] + temp[w.getIndex()];// TODO:-das
																	// array aus
																	// betweeness
					}

				}

			}
		}
		this.shortesPathCount = shortestPaths;
		for (Node v : this.g.getNodes()) {
			// TODO: das Array aus betwennes
		}
		return true;
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("after edge removal");
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException("after diff");
	}

}
