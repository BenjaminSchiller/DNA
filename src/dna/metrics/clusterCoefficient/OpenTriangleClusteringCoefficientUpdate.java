package dna.metrics.clusterCoefficient;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;
import dna.util.ArrayUtils;

@SuppressWarnings("rawtypes")
public class OpenTriangleClusteringCoefficientUpdate extends
		ClusteringCoefficient {

	public OpenTriangleClusteringCoefficientUpdate() {
		super("openTriangleClusteringCoefficientUpdate",
				ApplicationType.AfterUpdate);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition) {
			// TODO implement node addition @ openTriangle CC update
		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal @ openTriangle CC update
		} else if (u instanceof EdgeAddition) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode v = e.getSrc();
			DirectedNode w = e.getDst();
			// (1)
			for (DirectedNode x : v.getNeighbors()) {
				if (w.hasNeighbor(x)) {
					this.addTriangle(x);
				}
			}
			if (!v.hasEdge(new DirectedEdge(w, v))) {
				return true;
			}
			// (2)
			for (DirectedNode x : v.getNeighbors()) {
				if (w.hasEdge(new DirectedEdge(x, w))) {
					this.addTriangle(v);
				}
			}
			// (3)
			for (DirectedNode x : v.getNeighbors()) {
				if (w.hasEdge(new DirectedEdge(w, x))) {
					this.addTriangle(v);
				}
			}
			// (4)
			for (DirectedNode x : w.getNeighbors()) {
				if (v.hasEdge(new DirectedEdge(x, v))) {
					this.addTriangle(w);
				}
			}
			// (5)
			for (DirectedNode x : w.getNeighbors()) {
				if (v.hasEdge(new DirectedEdge(v, x))) {
					this.addTriangle(w);
				}
			}
			// (6)
			this.addPotentials(v, 2 * v.getNeighborCount() - 2);
			// (7)
			this.addPotentials(w, 2 * w.getNeighborCount() - 2);
		} else if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode v = e.getSrc();
			DirectedNode w = e.getDst();
			// (1)
			for (DirectedNode x : v.getNeighbors()) {
				if (w.hasNeighbor(x)) {
					this.removeTriangle(x);
				}
			}
			if (!v.hasEdge(new DirectedEdge(w, v))) {
				return true;
			}
			// (2)
			for (DirectedNode x : v.getNeighbors()) {
				if (w.hasEdge(new DirectedEdge(x, w))) {
					this.removeTriangle(v);
				}
			}
			// (3)
			for (DirectedNode x : v.getNeighbors()) {
				if (w.hasEdge(new DirectedEdge(w, x))) {
					this.removeTriangle(v);
				}
			}
			// (4)
			for (DirectedNode x : w.getNeighbors()) {
				if (v.hasEdge(new DirectedEdge(x, v))) {
					this.removeTriangle(w);
				}
			}
			// (5)
			for (DirectedNode x : w.getNeighbors()) {
				if (v.hasEdge(new DirectedEdge(v, x))) {
					this.removeTriangle(w);
				}
			}
			// (6)
			this.removePotentials(v, 2 * v.getNeighborCount());
			// (7)
			this.removePotentials(w, 2 * w.getNeighborCount());
		}
		return true;
	}

	private void addTriangle(DirectedNode origin) {
		this.triangleCount++;
		this.nodeTriangleCount[origin.getIndex()]++;
		this.update(origin.getIndex());
	}

	private void removeTriangle(DirectedNode origin) {
		this.triangleCount--;
		this.nodeTriangleCount[origin.getIndex()]--;
		this.update(origin.getIndex());
	}

	private void addPotentials(DirectedNode origin, int count) {
		this.potentialCount += count;
		this.nodePotentialCount[origin.getIndex()] += count;
		this.update(origin.getIndex());
	}

	private void removePotentials(DirectedNode origin, int count) {
		this.potentialCount -= count;
		this.nodePotentialCount[origin.getIndex()] -= count;
		this.update(origin.getIndex());
	}

	private void update(int index) {
		if (this.nodePotentialCount[index] == 0) {
			this.localCC[index] = 0;
		} else {
			this.localCC[index] = (double) this.nodeTriangleCount[index]
					/ this.nodePotentialCount[index];
		}
		if (this.potentialCount == 0) {
			this.globalCC = 0;
			this.averageCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
			this.averageCC = ArrayUtils.avg(this.localCC);
		}
	}

	@Override
	public boolean compute() {
		this.triangleCount = 0;
		this.potentialCount = 0;
		DirectedGraph g = (DirectedGraph) this.g;
		for (DirectedNode n : g.getNodes()) {
			this.nodeTriangleCount[n.getIndex()] = 0;
			this.nodePotentialCount[n.getIndex()] = 0;
			for (DirectedNode u : n.getNeighbors()) {
				for (DirectedNode v : n.getNeighbors()) {
					if (u.equals(v)) {
						continue;
					}
					this.nodePotentialCount[n.getIndex()]++;
					if (u.hasEdge(new DirectedEdge(u, v))) {
						this.nodeTriangleCount[n.getIndex()]++;
					}
				}
			}
			this.triangleCount += this.nodeTriangleCount[n.getIndex()];
			this.potentialCount += this.nodePotentialCount[n.getIndex()];
			if (this.nodePotentialCount[n.getIndex()] == 0) {
				this.localCC[n.getIndex()] = 0;
			} else {
				this.localCC[n.getIndex()] = (double) this.nodeTriangleCount[n
						.getIndex()]
						/ (double) this.nodePotentialCount[n.getIndex()];
			}
		}

		if (this.potentialCount == 0) {
			this.globalCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
		}
		this.averageCC = ArrayUtils.avg(this.localCC);

		return true;
	}

	@Override
	public boolean recompute() {
		return false;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
