package dna.metrics.betweenessCentrality;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

public class BCDyn extends BetweenessCentrality {

	public BCDyn() {
		super("BCDyn", ApplicationType.AfterUpdate);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition) {
			return applyAfterNodeAddition(u);
		} else if (u instanceof NodeRemoval) {
			return applyAfterNodeRemoval(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterEdgeAddition(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterEdgeRemoval(u);
		}
		return false;
	}

	private boolean applyAfterEdgeRemoval(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();

		UndirectedNode src = e.getNode1();
		UndirectedNode dst = e.getNode2();

		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
		// TODO:Levels einrichten
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		int[] distanceP = new int[this.g.getNodes().size()];
		boolean[] visited = new boolean[this.g.getNodes().size()];
		int[] shortestPaths = this.shortesPathCount;

		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}
		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
			for (UndirectedEdge ed : v.getEdges()) {
				UndirectedNode n = ed.getNode1();
				if (n == v)
					n = ed.getNode2();
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
		double[] temp = new double[this.g.getNodes().size()];
		for (int i = qLevel.length - 1; i >= 0; i--) {
			while (!qLevel[i].isEmpty()) {
				UndirectedNode w = qLevel[i].poll();
				for (UndirectedNode n : this.parentVertices[w.getIndex()]) {
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
		for (UndirectedNode v : (Collection<UndirectedNode>) this.g.getNodes()) {
			// TODO: das Array aus betwennes
		}
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void init_() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

}
